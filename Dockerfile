FROM openjdk:17-slim
MAINTAINER michael@mikuger.de
COPY config.yaml root/.smart-home/config.yaml
ADD build/distributions/SmartHomeDataCollector-0.2.x.tar .
CMD ["sh",  "SmartHomeDataCollector-0.2.x/bin/SmartHomeDataCollector"]