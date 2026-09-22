const http = require("http");
const fs = require("fs");
const path = require("path");
const { exec } = require("child_process");

const PORT = Number(process.env.PORT || 8050);
const PROJECT_DIR = path.resolve(__dirname, "..");
const KAFKA_SERVICE = process.env.KAFKA_SERVICE || "shared_kafka";
const BOOTSTRAP = process.env.KAFKA_BOOTSTRAP || "localhost:9092";
const BIN = "/opt/kafka/bin";
const REFRESH_MS = Number(process.env.REFRESH_MS || 4000);

let snapshot = { updatedAt: null, error: null, topics: [], shareGroups: [], deadLetters: [] };
let refreshing = false;

function kafka(tool, args) {
  const command = `docker compose exec -T ${KAFKA_SERVICE} ${BIN}/${tool} --bootstrap-server ${BOOTSTRAP} ${args}`;
  return new Promise((resolve, reject) => {
    exec(command, { cwd: PROJECT_DIR, maxBuffer: 50 * 1024 * 1024 }, (error, stdout, stderr) => {
      if (error && !stdout) return reject(new Error(stderr || error.message));
      resolve(stdout);
    });
  });
}

async function listTopics() {
  const out = await kafka("kafka-topics.sh", "--list");
  return out.split("\n").map((l) => l.trim()).filter((l) => l && !l.startsWith("__"));
}

async function endOffsets(topics) {
  if (!topics.length) return {};
  const out = await kafka("kafka-get-offsets.sh", `--topic-partitions '${topics.map((t) => t.replace(/\./g, "\\.")).join(",")}'`);
  const result = {};
  for (const line of out.split("\n")) {
    const m = line.trim().match(/^(.+):(\d+):(\d+)$/);
    if (!m) continue;
    result[m[1]] = result[m[1]] || {};
    result[m[1]][Number(m[2])] = Number(m[3]);
  }
  return result;
}

async function topicConfigs(topics) {
  const result = {};
  await Promise.all(
    topics.map(async (topic) => {
      const out = await kafka("kafka-configs.sh", `--describe --entity-type topics --entity-name ${topic}`);
      result[topic] = parseConfigs(out);
    })
  );
  return result;
}

function parseConfigs(out) {
  const configs = {};
  for (const line of out.split("\n")) {
    const m = line.trim().match(/^([\w.]+)=(\S*)\s+sensitive=/);
    if (m) configs[m[1]] = m[2];
  }
  return configs;
}

async function listShareGroups() {
  const out = await kafka("kafka-share-groups.sh", "--list");
  return out.split("\n").map((l) => l.trim()).filter(Boolean);
}

async function shareGroupOffsets() {
  const out = await kafka("kafka-share-groups.sh", "--describe --all-groups --offsets");
  const result = {};
  for (const line of out.split("\n")) {
    const parts = line.trim().split(/\s+/);
    if (parts.length < 5 || parts[0] === "GROUP") continue;
    const [group, topic, partition, startOffset, lag] = parts;
    if (!/^\d+$/.test(partition)) continue;
    result[group] = result[group] || [];
    result[group].push({ topic, partition: Number(partition), startOffset: toNumber(startOffset), lag: toNumber(lag) });
  }
  return result;
}

async function shareGroupMembers() {
  const out = await kafka("kafka-share-groups.sh", "--describe --all-groups --members --verbose");
  const result = {};
  for (const line of out.split("\n")) {
    const parts = line.trim().split(/\s+/);
    if (parts.length < 6 || parts[0] === "GROUP") continue;
    const [group, consumerId, host, clientId, partitions, epoch, assignment] = parts;
    result[group] = result[group] || [];
    result[group].push({ consumerId, host, clientId, partitions: Number(partitions), epoch: Number(epoch), assignment: (assignment || "").split(";").filter(Boolean) });
  }
  return result;
}

async function shareGroupStates() {
  const out = await kafka("kafka-share-groups.sh", "--describe --all-groups --state");
  const result = {};
  for (const line of out.split("\n")) {
    const m = line.trim().match(/^(\S+)\s+(\S+)\s+\((\d+)\)\s+(\S+)\s+(\d+)$/);
    if (m) result[m[1]] = { coordinator: m[2], state: m[4], members: Number(m[5]) };
  }
  return result;
}

async function shareGroupConfigs() {
  const out = await kafka("kafka-configs.sh", "--describe --entity-type groups --all");
  const result = {};
  let current = null;
  for (const line of out.split("\n")) {
    const header = line.match(/configs for group (\S+) are:/);
    if (header) { current = header[1]; result[current] = {}; continue; }
    const m = line.trim().match(/^([\w.]+)=(\S*)\s+sensitive=/);
    if (m && current) result[current][m[1]] = m[2];
  }
  return result;
}

async function readDeadLetters(topic, count) {
  if (!count) return [];
  const out = await kafka(
    "kafka-console-consumer.sh",
    `--topic ${topic} --from-beginning --max-messages ${count} --timeout-ms 4000 ` +
      "--formatter-property print.headers=true --formatter-property print.key=true " +
      "--formatter-property print.timestamp=true --formatter-property print.offset=true " +
      "--formatter-property print.partition=true --formatter-property key.separator=$'\\t'"
  );
  const records = [];
  for (const line of out.split("\n")) {
    if (!line.trim()) continue;
    const parts = line.split("\t");
    if (parts.length < 6) continue;
    const [timestamp, partition, offset, headersRaw, key, ...body] = parts;
    const headers = {};
    for (const h of headersRaw.split(",")) {
      const i = h.indexOf(":");
      if (i > 0) headers[h.slice(0, i)] = h.slice(i + 1);
    }
    records.push({
      topic,
      partition: Number(partition.replace("Partition:", "")),
      offset: Number(offset.replace("Offset:", "")),
      timestamp: Number(timestamp.replace("CreateTime:", "")),
      key,
      body: body.join("\t"),
      originalTopic: headers["__dlq.errors.topic"] || "unknown",
      originalPartition: Number(headers["__dlq.errors.partition"] || -1),
      originalOffset: Number(headers["__dlq.errors.offset"] || -1),
      failedShareGroup: headers["__dlq.errors.group"] || "unknown",
      deliveryCount: Number(headers["__dlq.errors.delivery.count"] || 0),
      cause: headers["__dlq.errors.message"] || "unknown",
    });
  }
  return records;
}

function toNumber(value) {
  const n = Number(value);
  return Number.isFinite(n) ? n : null;
}

async function buildSnapshot() {
  const [topicNames, groupNames] = await Promise.all([listTopics(), listShareGroups()]);
  const [offsets, configs, groupOffsets, members, states, groupConfigs] = await Promise.all([
    endOffsets(topicNames),
    topicConfigs(topicNames),
    shareGroupOffsets(),
    shareGroupMembers(),
    shareGroupStates(),
    shareGroupConfigs(),
  ]);

  const topics = topicNames.map((name) => {
    const partitions = offsets[name] || {};
    const total = Object.values(partitions).reduce((a, b) => a + b, 0);
    const cfg = configs[name] || {};
    return {
      name,
      partitions: Object.keys(partitions).length,
      endOffset: total,
      isDeadLetter: cfg["errors.deadletterqueue.group.enable"] === "true" || name.startsWith("dlq."),
      retentionMs: cfg["retention.ms"],
      cleanupPolicy: cfg["cleanup.policy"],
    };
  });

  const shareGroups = groupNames.map((name) => {
    const cfg = groupConfigs[name] || {};
    const partitions = groupOffsets[name] || [];
    const enriched = partitions.map((p) => {
      const end = (offsets[p.topic] || {})[p.partition];
      const pending = p.lag ?? 0;
      const processed = end == null ? null : Math.max(end - pending, 0);
      return { ...p, endOffset: end ?? null, processed, pending };
    });
    return {
      name,
      state: states[name]?.state || "Unknown",
      memberCount: states[name]?.members ?? (members[name] || []).length,
      members: members[name] || [],
      partitions: enriched,
      topics: [...new Set(enriched.map((p) => p.topic))],
      processed: enriched.reduce((a, p) => a + (p.processed || 0), 0),
      pending: enriched.reduce((a, p) => a + (p.pending || 0), 0),
      deadLetterTopic: cfg["errors.deadletterqueue.topic.name"] || "",
      copyRecord: cfg["errors.deadletterqueue.copy.record.enable"] === "true",
      deliveryCountLimit: Number(cfg["share.delivery.count.limit"] || 0),
      autoOffsetReset: cfg["share.auto.offset.reset"] || "",
      recordLockMs: Number(cfg["share.record.lock.duration.ms"] || 0),
    };
  });

  const deadLetterTopics = topics.filter((t) => t.isDeadLetter);
  const deadLetters = (await Promise.all(deadLetterTopics.map((t) => readDeadLetters(t.name, t.endOffset)))).flat();

  return { updatedAt: new Date().toISOString(), error: null, topics, shareGroups, deadLetters };
}

async function refresh() {
  if (refreshing) return;
  refreshing = true;
  try {
    snapshot = await buildSnapshot();
  } catch (error) {
    snapshot = { ...snapshot, error: error.message, updatedAt: new Date().toISOString() };
  } finally {
    refreshing = false;
  }
}

refresh();
setInterval(refresh, REFRESH_MS);

http
  .createServer((req, res) => {
    if (req.url.startsWith("/api/state")) {
      res.writeHead(200, { "Content-Type": "application/json" });
      return res.end(JSON.stringify(snapshot));
    }
    const file = path.join(__dirname, "public", req.url === "/" ? "index.html" : req.url);
    if (!file.startsWith(path.join(__dirname, "public")) || !fs.existsSync(file)) {
      res.writeHead(404);
      return res.end("Not found");
    }
    const type = file.endsWith(".js") ? "text/javascript" : file.endsWith(".css") ? "text/css" : "text/html";
    res.writeHead(200, { "Content-Type": type });
    fs.createReadStream(file).pipe(res);
  })
  .listen(PORT, () => console.log(`Kafka Queues viewer on http://localhost:${PORT}`));
