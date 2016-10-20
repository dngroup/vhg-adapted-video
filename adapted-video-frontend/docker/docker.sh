#/bin/bash
#docker run -it -e PORT=8080 -e RABBIT_HOST=172.17.42.1 -e RABBIT_PORT=5672  dngroup/mwc-frontend
docker build -t dngroup/mwc-frontend .
docker push dngroup/mwc-frontend
