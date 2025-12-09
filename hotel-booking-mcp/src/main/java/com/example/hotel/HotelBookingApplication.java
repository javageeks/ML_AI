package com.example.hotel;

import com.example.hotel.service.BookingTool;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class HotelBookingApplication
{
    public static void main(String[] args) {
        SpringApplication.run(HotelBookingApplication.class, args);
    }

    @Bean
    public List<ToolCallback> findTools(BookingTool bookingTool) {
        System.out.println("------ "+ ToolCallbacks.from(bookingTool).toString());
        List.of(ToolCallbacks.from(bookingTool)).stream().forEach(t ->
                System.out.println("Tools on MCP Server "+t.getToolDefinition().toString())
        );
        return List.of(ToolCallbacks.from(bookingTool));
    }
}
