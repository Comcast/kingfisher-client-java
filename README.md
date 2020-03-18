# Kingfisher Client Java

Kingfisher Client Java Library helps access the kingfisher backend api for the purposes of testing.

## Getting Started

### Prerequisites
* API key/ Bearer token

You can get an API key from the developer tools.
> Click on your user name at the top right corner of the page and generate an API key by clicking on the `API Keys` tab.

 *Note* : You can specify the validity duration of the key. By default it is one week.

### Installing

You can install the library from maven central. If you are using maven then you can add the following to your pom.xml file:
```
<dependency>
  <groupId>com.comcast.ibis</groupId>
  <artifactId>kingfisher-client-java</artifactId>
  <version>1.0.0</version>
</dependency>
```

Or if you are using gradle you can add:

```
implementation 'com.comcast.ibis:kingfisher-client-java:1.0.0@jar'
```

### Usage

```
KingfisherClient kf = new KingfisherClient.KingfisherClientBuilder().setToken(System.getenv("apikey"), KingfisherClient.KingfisherClientBuilder.TokenType.APIKEY).setHost(Constants.KINGFISHER_SERVICE).build();
kf.start();
List<Device> devices = kf.searchDevices(Search.or(Search.deviceID("<device id>")));
kf.reserve(devices);
devices.forEach(d-> {
    d.deeplink("<deeplink url>");
});
kf.release(devices);
kf.stop();
```
