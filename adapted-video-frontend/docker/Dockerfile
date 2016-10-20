FROM java:8-jre
MAINTAINER David Bourasseau <dbourasseau@viotech.net>
#RUN apt-get update
RUN mkdir -p /var/www/dummy
COPY adapted-video-frontend.jar frontend.jar
CMD java -jar frontend.jar --host 0.0.0.0 --port $PORT --rabbit-host $RABBIT_HOST --rabbit-port $RABBIT_PORT
