package com.yax.messagequeue.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

/**
 * ${DESCRIPTION}
 *
 * @author yax
 * @create 2019-03-11 14:48
 **/
@Configuration
public class SpringConfig {
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.timeout}")
    private int redisTimeout;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.database}")
    private int redisDb;

    @Value("${spring.redis.lettuce.pool.max-active}")
    private int maxActive;

    @Value("${spring.redis.lettuce.pool.max-wait}")
    private int maxWait;

    @Value("${spring.redis.lettuce.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.lettuce.pool.min-idle}")
    private int minIdle;


    @Bean
    public GenericObjectPoolConfig  genericObjectPoolConfig(){
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(maxIdle);
        genericObjectPoolConfig.setMinIdle(minIdle);
        genericObjectPoolConfig.setMaxTotal(maxActive);
        genericObjectPoolConfig.setMaxWaitMillis(maxWait);
        return genericObjectPoolConfig;
    }
    @Bean
    public RedisConnectionFactory connectionFactory(GenericObjectPoolConfig genericObjectPoolConfig) {

        // 单机版配置
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(redisDb);
        redisStandaloneConfiguration.setHostName(redisHost);
        redisStandaloneConfiguration.setPort(redisPort);
        redisStandaloneConfiguration.setPassword(RedisPassword.of(password));

        // 集群版配置
//        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
//        String[] serverArray = clusterNodes.split(",");
//        Set<RedisNode> nodes = new HashSet<RedisNode>();
//        for (String ipPort : serverArray) {
//            String[] ipAndPort = ipPort.split(":");
//            nodes.add(new RedisNode(ipAndPort[0].trim(), Integer.valueOf(ipAndPort[1])));
//        }
//        redisClusterConfiguration.setPassword(RedisPassword.of(password));
//        redisClusterConfiguration.setClusterNodes(nodes);
//        redisClusterConfiguration.setMaxRedirects(maxRedirects);
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(redisTimeout))
                .poolConfig(genericObjectPoolConfig)
                .build();

        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisStandaloneConfiguration,clientConfig);
        return factory;
    }
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate redisTemplate=new RedisTemplate();
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }


    //@Bean
    /*RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        //订阅了一个叫queue的通道
        container.addMessageListener(listenerAdapter, new PatternTopic("queue"));
        //这个container 可以添加多个 messageListener
        return container;
    }*/

    /**
     * 消息监听器适配器，绑定消息处理器，利用反射技术调用消息处理器的业务方法
     * @param
     * @return
     */
    /*@Bean
    MessageListenerAdapter listenerAdapter(MessageReceiver receiver) {
        //这个地方 是给messageListenerAdapter 传入一个消息接受的处理器，利用反射的方法调用“receiveMessage”
        //也有好几个重载方法，这边默认调用处理器的方法 叫handleMessage 可以自己到源码里面看
        MessageListenerAdapter messageListenerAdapter=new MessageListenerAdapter(receiver, "receiveMessage");
        RedisSerializer fastJsonSerializer = new FastJsonRedisSerializer<>(Object.class);
        messageListenerAdapter.setSerializer(fastJsonSerializer);
        return messageListenerAdapter;
    }*/


    /*@Bean
    public RedisQueueListenerContainer container(RedisTemplate redisTemplate, QueueListenerAdapter listenerAdapter,PushClient pushClient){
        RedisQueueListenerContainer redisQueueListenerContainer=new RedisQueueListenerContainer();
        redisQueueListenerContainer.setRedisTemplate(redisTemplate);
        //redisQueueListenerContainer.setTimeOut(50);//默认50
        redisQueueListenerContainer.setPushConfig(pushClient,(blockQueue, messageModel, e) -> {
            if(messageModel.getRetryCount()<3){
                blockQueue.retryPush(TimeUnit.MINUTES,messageModel,1,null);
            }
        });
        redisQueueListenerContainer.addMessageListener(listenerAdapter,"queue",1,1);
        return redisQueueListenerContainer;
    }*/
    /*@Bean
    public RedisQueueListenerContainer container(RedisConnectionFactory connectionFactory, QueueListenerAdapter listenerAdapter){
        RedisQueueListenerContainer redisQueueListenerContainer=new RedisQueueListenerContainer();
        redisQueueListenerContainer.setRedisConnectionFactory(connectionFactory);
        redisQueueListenerContainer.addMessageListener(listenerAdapter,"queue",1,50L);
        return redisQueueListenerContainer;
    }*/
    /**
     * 消息监听器适配器，绑定消息处理器，利用反射技术调用消息处理器的业务方法
     * @param
     * @return
     */
    /*@Bean
    public QueueListenerAdapter  listenerAdapter(MessageReceiver messageReceiver){
        QueueListenerAdapter queueListenerAdapter=new QueueListenerAdapter(messageReceiver,"receiveMessage");
        return queueListenerAdapter;
    }*/
    /**
     * 把推送客户端注入ioc容器
     * @return
     */
    /*@Bean
    public PushClient pushClient(RedisConnectionFactory connectionFactory){
        return new PushClient(connectionFactory);
    }*/
    /*@Bean
    public PushClient pushClient(RedisTemplate redisTemplate){
        return new PushClient(redisTemplate);
    }*/
}
