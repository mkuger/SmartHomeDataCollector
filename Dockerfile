FROM openjdk:13-alpine
MAINTAINER michael@mikuger.de
ADD build/distributions/SmartHomeDataCollector-0.2.x.tar .
CMD ["sh",  "SmartHomeDataCollector-0.2.x/bin/SmartHomeDataCollector"]