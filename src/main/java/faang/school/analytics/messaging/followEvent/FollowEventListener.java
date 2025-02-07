package faang.school.analytics.messaging.followEvent;

import faang.school.analytics.dto.followEvent.FollowEventDto;
import faang.school.analytics.service.comment.CommentEventWorker;
import faang.school.analytics.service.follow.FollowEventWorker;
import faang.school.analytics.util.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FollowEventListener implements MessageListener {
    private final JsonMapper mapper;
    private final FollowEventWorker followEventWorker;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("FollowEventListener has received a message: " + message.toString());
        mapper.toObject(message.toString(), FollowEventDto.class)
                .ifPresent(followEventWorker::saveFollowEvent);
        log.info(message+ " " + "has been handled");
    }
}
