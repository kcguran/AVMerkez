import com.avmerkez.mallservice.event.MallCreatedEvent;
import com.avmerkez.mallservice.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MallServiceImpl implements MallService {
    private final MallRepository mallRepository;
    private final MallMapper mallMapper;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public MallDto createMall(CreateMallRequest request) {
        Mall mall = mallMapper.toEntity(request);
        Mall saved = mallRepository.save(mall);
        // Event oluştur ve gönder
        MallCreatedEvent event = new MallCreatedEvent(
            saved.getId(),
            saved.getName(),
            saved.getCity(),
            saved.getDistrict(),
            saved.getDescription(),
            saved.getLatitude(),
            saved.getLongitude()
        );
        kafkaProducerService.sendMallCreatedEvent(event);
        return mallMapper.toDto(saved);
    }
} 