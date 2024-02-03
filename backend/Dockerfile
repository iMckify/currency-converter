ARG repo=currency-converter
ARG app=backend

FROM ubuntu:22.04 as machine
ARG DEBIAN_FRONTEND=noninteractive
RUN apt update

FROM machine as intermediate

ARG github_secret
ARG repo
ARG branch=main

# setup git, git clone
RUN apt install git-all -y
RUN git clone https://imckify:${github_secret}@github.com/iMckify/${repo}.git

WORKDIR ${repo}
RUN pwd
RUN git checkout ${branch}
RUN git status

FROM machine

ARG repo
ARG app

LABEL maintainer="rokas.mockevicius@gmail.com"

LABEL description="This is custom Docker Image for \
the app startup"

# setup image dir arguments
ARG dir_srv=/srv
ARG dir_src=${dir_srv}/${repo}

# copy cloned repo from intermediate to the image
COPY --from=intermediate ${repo} ${dir_src}

# setup Linux, java, git
RUN apt install openjdk-17-jdk-headless -y

# run java
WORKDIR ${dir_src}/${app}
RUN ./mvnw spring-boot:run

# entrypoint for the docker run
ENTRYPOINT ["./mvnw", "spring-boot:run"]