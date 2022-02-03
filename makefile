java:
	docker build dockerfiles/java -t log4shell:java

server:
	docker build dockerfiles/attacker/server -t log4shell:server


victim: java
	docker build dockerfiles/victim/site -t log4shell:victim

builder: java
	docker build dockerfiles/builder -t log4shell:builder

logger:
	docker build dockerfiles/attacker/logger -t log4shell:logger

all: java server victim builder logger