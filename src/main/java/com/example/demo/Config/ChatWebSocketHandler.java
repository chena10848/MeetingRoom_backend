package com.example.demo.Config;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.CloseStatus;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.example.demo.Other.PrintStreamColor;

@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    private final PrintStreamColor printStreamColor;
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    public ChatWebSocketHandler(PrintStreamColor printStreamColor) {
        this.printStreamColor = printStreamColor;
    }

    // 连接建立后调用
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("WebSocket connection established: " + session.getId());
    }

    // 处理接收到的消息
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        this.printStreamColor.printlnGreen("Received message: " + message.getPayload());

        // 发送回响应消息
        session.sendMessage(new TextMessage("Message received: " + message.getPayload()));
    }

    // 处理传输错误
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("WebSocket error: " + exception.getMessage());
    }

    // 连接关闭后调用
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println("WebSocket connection closed: " + session.getId());
    }

    // 是否支持部分消息
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
