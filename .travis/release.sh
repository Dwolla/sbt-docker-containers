#!/usr/bin/env bash

set -o errexit -o nounset

USERNAME="Dwolla Bot"

commit_username=$(git log -n1 --format=format:"%an")
if [[ "$commit_username" == "$USERNAME" ]]; then
  echo "Refusing to release a commit created by this script."
  exit 0
fi

if [ "$TRAVIS_BRANCH" != "master" ]; then
  echo "Only the master branch will be released. This branch is $TRAVIS_BRANCH."
  exit 0
fi

git config user.name "$USERNAME"
git config user.email "dev+dwolla-bot@dwolla.com"

git remote add release https://$GH_TOKEN@github.com/Dwolla/sbt-docker-containers.git
git fetch release

git clean -dxf
git checkout master
git branch --set-upstream-to=release/master

MASTER=$(git rev-parse HEAD)
if [ "$TRAVIS_COMMIT" != "$MASTER" ]; then
  echo "Checking out master set HEAD to $MASTER, but Travis was building $TRAVIS_COMMIT, so refusing to continue."
  exit 0
fi

sbt clean "release with-defaults"
