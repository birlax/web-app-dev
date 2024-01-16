
# 

### Make sure 

- awscli is installed
- region is set to "us-west-2" use same in all the places

### Base queue url
- http://localhost:9324

### Access UI 
- http://localhost:9325

### Pre-configured queues

Queue-Name | Queue publish url |
--- | ---
default|http://localhost:9324/queue/default
redirect-queue|http://localhost:9324/queue/redirect-queue
audit-queue|http://localhost:9324/queue/audit-queue
dead-letters-queue|http://localhost:9324/queue/dead-letters-queue

### Publish message
```
aws --endpoint-url http://localhost:9324 sqs send-message --queue-url http://localhost:9324/queue/default --message-body "Hello, queue!"

```