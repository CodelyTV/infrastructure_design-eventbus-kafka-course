package tv.codely.mooc.courses.infrastructure.email;

import tv.codely.mooc.courses.domain.CourseId;
import tv.codely.mooc.courses.domain.CourseName;
import tv.codely.mooc.courses.domain.CourseRenamedEmailNotSent;
import tv.codely.mooc.courses.domain.CourseRenamedEmailSenderGateway;
import tv.codely.shared.domain.Logger;
import tv.codely.shared.domain.Service;
import tv.codely.shared.infrastructure.bus.event.kafka.KafkaDomainEventLockRenewer;

import java.time.Duration;

@Service
public final class SendgridCourseRenamedEmailSender implements CourseRenamedEmailSenderGateway {
    private static final int      MAX_ATTEMPTS            = 3;
    private static final int      FAILURES_BEFORE_SUCCESS = 2;
    private static final Duration SENDGRID_LATENCY        = Duration.ofSeconds(3);

    private final KafkaDomainEventLockRenewer lockRenewer;
    private final Logger                      logger;

    public SendgridCourseRenamedEmailSender(KafkaDomainEventLockRenewer lockRenewer, Logger logger) {
        this.lockRenewer = lockRenewer;
        this.logger      = logger;
    }

    @Override
    public void send(CourseId id, CourseName name) throws CourseRenamedEmailNotSent {
        for (int attempt = 1; ; attempt++) {
            try {
                callSendgrid(id, name, attempt);

                return;
            } catch (SendgridUnavailable error) {
                if (attempt >= MAX_ATTEMPTS) {
                    throw new CourseRenamedEmailNotSent(id, error.getMessage());
                }

                logger.warning(
                    String.format(
                        "[Sendgrid] Attempt %d/%d for course <%s> failed (%s). Renewing the Kafka lock before retrying",
                        attempt,
                        MAX_ATTEMPTS,
                        id.value(),
                        error.getMessage()
                    )
                );

                lockRenewer.renew();
            }
        }
    }

    private void callSendgrid(CourseId id, CourseName name, int attempt) throws SendgridUnavailable {
        logger.info(String.format("[Sendgrid] POST /v3/mail/send for course <%s> (attempt %d)", id.value(), attempt));

        waitSendgridResponse();

        if (attempt <= FAILURES_BEFORE_SUCCESS) {
            throw new SendgridUnavailable();
        }

        logger.info(String.format("[Sendgrid] 202 Accepted: the course <%s> is now named <%s>", id.value(), name.value()));
    }

    private void waitSendgridResponse() {
        try {
            Thread.sleep(SENDGRID_LATENCY);
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
        }
    }

    private static final class SendgridUnavailable extends Exception {
        SendgridUnavailable() {
            super("Sendgrid responded 503 Service Unavailable");
        }
    }
}
