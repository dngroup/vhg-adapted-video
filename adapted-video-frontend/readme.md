# Installation #

## dependencies ##

    sudo apt-get install rabbitmq-server openjdk-7-jdk

## configuring rabbit mq ##

the rabbit mq configuration file is /etc/rabbitmq/rabbitmq.config

```erlang
  [
    {mnesia, [{dump_log_write_threshold, 1000}]},
    {rabbit, [{tcp_listeners, [{"0.0.0.0",5672}]}]}
  ].
```


