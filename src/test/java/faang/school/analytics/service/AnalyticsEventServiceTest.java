package faang.school.analytics.service;

import faang.school.analytics.dto.AnalyticsDto;
import faang.school.analytics.dto.AnalyticsFilterDto;
import faang.school.analytics.dto.LikeEventDto;
import faang.school.analytics.dto.PostViewEvent;
import faang.school.analytics.dto.followEvent.FollowEventDto;
import faang.school.analytics.mapper.AnalyticsEventMapperImpl;
import faang.school.analytics.mapper.EventMapper;
import faang.school.analytics.model.AnalyticsEvent;
import faang.school.analytics.model.EventType;
import faang.school.analytics.repository.AnalyticsEventRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AnalyticsEventServiceTest {

    @Mock
    AnalyticsEventRepository analyticsEventRepository;

    @Mock
    EventMapper eventMapper;

    @Spy
    private AnalyticsEventMapperImpl mapper;

    @InjectMocks
    AnalyticsEventService analyticsEventService;

    AnalyticsEvent analyticsEvent;
    AnalyticsDto analyticsDto;
    AnalyticsFilterDto filterDto;
    FollowEventDto followEventDto;
    LikeEventDto likeEventDto;

    @BeforeEach
    void setUp() {
        analyticsEvent = AnalyticsEvent.builder().receiverId(1L).eventType(EventType.FOLLOWER).receivedAt(LocalDateTime.now()).build();
        analyticsDto = AnalyticsDto.builder().receiverId(1L).eventType(EventType.FOLLOWER).receivedAt(LocalDateTime.now()).build();
        filterDto = new AnalyticsFilterDto(1L, EventType.FOLLOWER, null, null);
        followEventDto = new FollowEventDto();

        likeEventDto = LikeEventDto
                .builder()
                .postId(1L)
                .authorPostId(1L)
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .userId(1L)
                .build();
    }

    @Test
    void saveLikeEvent_ShouldMapCorrectly() {
        AnalyticsEvent actual = mapper.toEvent(likeEventDto);

        Assertions.assertEquals(mockAnalyticsLikeEvent(), actual);
    }

    @Test
    void saveLikeEvent_ShouldBeSaved() {
        analyticsEventService.likeEventSave(likeEventDto);

        Mockito.verify(analyticsEventRepository).save(Mockito.any(AnalyticsEvent.class));
    }

    @Test
    void savePostEvent_ShouldMapCorrectly() {
        AnalyticsEvent actual = mapper.toAnalyticsEvent(mockPostViewEvent());

        Assertions.assertEquals(mockAnalyticsEvent(), actual);
    }

    @Test
    void savePostEvent_ShouldBeSaved() {
        analyticsEventService.savePostEvent(mockPostViewEvent());

        Mockito.verify(analyticsEventRepository).save(Mockito.any(AnalyticsEvent.class));
    }

    @Test
    void FollowEventSaveTest() {
        Mockito.when(eventMapper.toEntity(followEventDto)).thenReturn(analyticsEvent);
        analyticsEventService.followEventSave(followEventDto);
        Mockito.verify(analyticsEventRepository, Mockito.times(1)).save(analyticsEvent);
    }

    @Test
    void getAnalyticsTest() {
        Mockito.when(eventMapper.toDto(analyticsEvent)).thenReturn(analyticsDto);
        Mockito.when(analyticsEventRepository.findByReceiverIdAndEventType(Mockito.anyLong(), Mockito.any(EventType.class))).thenReturn(List.of(analyticsEvent));

        var result = analyticsEventService.getAnalytics(filterDto);
        Assertions.assertEquals(result, List.of(analyticsDto));
    }

    @Test
    void getAnalyticsEmptyListTest() {
        Mockito.when(analyticsEventRepository.findByReceiverIdAndEventType(Mockito.anyLong(), Mockito.any(EventType.class))).thenReturn(List.of(analyticsEvent));
        filterDto.setStart(LocalDateTime.MAX);

        var result = analyticsEventService.getAnalytics(filterDto);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Save analytics event")
    void saveAnalyticsEvent() {
        AnalyticsEvent analyticsEvent = AnalyticsEvent.builder()
                .id(null)
                .actorId(123L)
                .receiverId(67890L)
                .eventType(EventType.FUND_RAISED)
                .receivedAt(LocalDateTime.parse("2023-09-07T12:34:56"))
                .build();
        analyticsEventService.save(analyticsEvent);

        Mockito.verify(analyticsEventRepository, Mockito.times(1)).save(analyticsEvent);
    }

    private PostViewEvent mockPostViewEvent() {
        return new PostViewEvent(1L, 1L, null, 1L);
    }

    private AnalyticsEvent mockAnalyticsEvent() {
        return AnalyticsEvent.builder()
                .id(null)
                .receiverId(1L)
                .actorId(1L)
                .eventType(EventType.POST_VIEW)
                .build();
    }

    private AnalyticsEvent mockAnalyticsLikeEvent() {
        return AnalyticsEvent.builder()
                .id(null)
                .receiverId(1L)
                .actorId(1L)
                .eventType(EventType.POST_LIKE)
                .receivedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
    }
}
