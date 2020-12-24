# Growing Object-Oriented Software with Tests

This is a fork of the official repo with the [worked example code](https://github.com/sf105/goos-code) for "Growing Object-Oriented Software, Guided by Tests" by Steve Freeman and Nat Pryce (2010).

## How to run the tests

1. Start an Openfire XMPP server with Docker (or modify https://hub.docker.com/r/sameersbn/openfire/dockerfile to your taste and build it yourself).
```
mkdir -p /tmp/docker/openfire
docker run --name openfire -d --restart=always \
  --publish 9090:9090 --publish 5222:5222 --publish 7777:7777 \
  --volume /tmp/docker/openfire:/var/lib/openfire \
  sameersbn/openfire:3.10.3-19
```
2. Open the Openfire admin interface at `localhost:9090` and configure Openfire manually
- Server > Server Manager > Server Information > Edit Properties > Servername: localhost
- Server > Server Settings > Resource Policy > Set Conflict Policy: Never kick
- Server > Server Settings > Offline Messages > Offline Message Policy: Drop
- create end-to-end test users
- Users/Groups > Create New User
    - auction-item-54321, auction
    - auction-item-65432, auction
    - sniper, sniper
3. Install ant with your favorite package management system, e.g. `brew install ant`.
4. Run `ant build`. If there are failing tests, run them individually to get more detailed feedback.
