# ntc-jnats
ntc-jnats is module [NAST](https://nats.io/) java client.  

## 1. Publish-Subscribe
![Publish-Subscribe](/img/pubsub.png)

### Publisher
```java
String subj = "msg.test";
for (int i=0; i<10; i++) {
    String msg = "hello " + i;
    NPub.getInstance("pub-notify").publish(subj, msg);
    log.info("Published PubSub ["+subj+"] : '"+msg+"'");
}
```

### Subscriber
```java
public static class NSubscriber extends NSub {
    private final Logger log = LoggerFactory.getLogger(NSubscriber.class);

    public NSubscriber(String name) throws IOException, InterruptedException {
        super(name);
    }

    @Override
    public void execute(byte[] data) {
        try {
            String msg = new String(data, StandardCharsets.UTF_8);
            log.info("NSubscriber received on PubSub ["+getSubject()+"]: '"+msg+"'");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}

public static void main(String[] args) {
    try {
        NSubGroup nsubGroup = new NSubGroup();
        for (int i=0; i< 2; i++) {
            NSubscriber ns = new NSubscriber("sub-notify");
            nsubGroup.add(ns);
        }
        nsubGroup.start();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

## 2. Queue Groups
![Queue Groups](/img/queue.png) <!-- .element height="50%" width="50%" -->

### Queue Worker
```java
public static class NWorkerEmail extends NWorker {
    private final Logger log = LoggerFactory.getLogger(NWorkerEmail.class);

    public NWorkerEmail(String name) throws IOException, InterruptedException {
        super(name);
    }

    @Override
    public void execute(byte[] data) {
        try {
            String msg = new String(data, StandardCharsets.UTF_8);
            log.info("NWorkerEmail["+getGroup()+"] received on QueueWorker["+getSubject()+"]: '"+msg+"'");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}

public static void main(String[] args) {
    try {
        NWorkerGroup workerGroup = new NWorkerGroup();
        for (int i=0; i<2; i++) {
            NWorkerEmail nw = new NWorkerEmail("worker-email");
            workerGroup.add(nw);
        }
        workerGroup.start();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

### Publisher
```java
String subj = "worker.email";
for (int i=0; i<10; i++) {
    String msg = "hello " + i;
    NPub.getInstance("pub-notify").publish(subj, msg);
    log.info("Published PubSub ["+subj+"] : '"+msg+"'");
}
```

## 3. Request-Reply
![Request-Reply](/img/reqres.png) <!-- .element height="50%" width="50%" -->

### Request
```java
String subj = "reqres";
for (int i=0; i<10; i++) {
    String msg = "this is request " + i;
    Message resp = NReq.getInstance("req-db").publish(subj, msg);
    log.info("NReq Requested ["+subj+"] : '"+msg+"'");
    log.info("NReq Received  ["+resp.getSubject()+"] : '"+new String(resp.getData(), StandardCharsets.UTF_8)+"'");
}
```

### Reply
```java
public static class NResQueryDB extends NRes {
    private final Logger log = LoggerFactory.getLogger(NResQueryDB.class);
    private String reply = "this is response ==> ";

    public NResQueryDB(String name) throws IOException, InterruptedException {
        super(name);
    }

    @Override
    public void execute(Message msg) {
        try {
            String data = new String(msg.getData(), StandardCharsets.UTF_8);
            log.info("NRes["+getGroup()+"] Received on QueueNRes["+getSubject()+"]: '"+data+"'");
            String datares = reply + data;
            reply(msg, datares);
            log.info("NRes["+getGroup()+"] Reply on QueueNRes["+getSubject()+"]: '"+datares+"'");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}

public static void main(String[] args) {
    try {
        NResGroup resGroup = new NResGroup();
        for (int i=0; i<2; i++) {
            NResQueryDB res = new NResQueryDB("res-db");
            resGroup.add(res);
        }
        resGroup.start();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```


## License
This code is under the [Apache Licence v2](https://www.apache.org/licenses/LICENSE-2.0).  
