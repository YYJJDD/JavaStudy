package com.jingdyang.trigger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MessageTaskDispatchFactory {

    private final Map<String, Task> allTaskImplements;

    @Autowired
    public MessageTaskDispatchFactory(List<Task> taskList) {
        this.allTaskImplements = taskList.stream().collect(Collectors.toMap(Task::getId, Function.identity()));
    }

    public MessageTaskDispatch getInstance(String taskId) {
        return new MessageTaskDispatchImpl(allTaskImplements.get(taskId));
    }

}
