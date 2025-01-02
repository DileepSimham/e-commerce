//package com.e_commerce.service;
//
//import com.e_commerce.entity.Order;
//import com.e_commerce.repository.OrderRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class OrderStatusUpdateService {
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Autowired
//    private KafkaProducer kafkaProducer;
//
//    private final List<String> statusSequence = List.of("PENDING", "PROCESSING", "OUT_FOR_DELIVERY", "DELIVERED");
//
//    @Scheduled(fixedRate = 10000) // Every 2 Sec
//    public void updateOrderStatuses() {
//        List<Order> pendingOrders = orderRepository.findByOrderStatus("PENDING");
//
//        for (Order order : pendingOrders) {
//            String currentStatus = order.getOrderStatus();
//            int nextIndex = statusSequence.indexOf(currentStatus) + 1;
//
//            if (nextIndex < statusSequence.size()) {
//                String nextStatus = statusSequence.get(nextIndex);
//                order.setOrderStatus(nextStatus);
//                orderRepository.save(order);
//
//                // Send the updated order entity to Kafka
//                kafkaProducer.sendOrderStatusUpdate(order);
//            }
//        }
//    }
//}
