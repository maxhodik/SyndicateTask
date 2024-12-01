package dto;

import software.amazon.awssdk.services.dynamodb.endpoints.internal.Value;

public class ResponseDto {
    private Integer statusCode;
    private EventDto eventDto;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public EventDto getEventDto() {
        return eventDto;
    }

    public ResponseDto() {
    }

    public void setEventDto(EventDto eventDto) {
        this.eventDto = eventDto;
    }
}
