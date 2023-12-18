package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.MapperItem;
import ru.practicum.shareit.request.Request;

import java.util.stream.Collectors;

public class MapperRequest {

    public static RequestDto mapToRequestDto(Request request) {
        RequestDto requestDto = new RequestDto(request.getId(),request.getDescription(),request.getCreated(),request.getUser(),null);
        if(request.getItems()!=null){
            requestDto.setItems(request.getItems()
                    .stream().map(i-> MapperItem.mapToItemDto(i)).collect(Collectors.toList()));
        }
        return requestDto;
    }

}
