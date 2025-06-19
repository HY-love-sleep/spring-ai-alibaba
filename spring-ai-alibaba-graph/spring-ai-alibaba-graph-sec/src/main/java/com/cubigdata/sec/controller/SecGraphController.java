package com.cubigdata.sec.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author yHong
 * @version 1.0
 * @since 2025/6/19 15:42
 */
@RestController
@RequestMapping("/sec/graph")
public class SecGraphController {
    private final CompiledGraph compiledGraph;

    public SecGraphController(@Qualifier("secGraph") StateGraph stateGraph) throws GraphStateException {
        this.compiledGraph = stateGraph.compile();
    }

    @GetMapping("/chat")
    public Map<String, Object> simpleChat(@RequestParam("fieldName") String fieldName) throws Exception {

        return compiledGraph.invoke(Map.of("field", fieldName)).get().data();
    }
}
