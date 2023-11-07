<link rel="stylesheet" type="text/css" href="style.css">

# newsgears-rss

newsgears-rss is the feed server component of the NewsGears RSS platform.  

## 1. Quick-start using pre-built containers:

If you don't want to do development, just start the feed server using pre-built containers:

```
docker ...
```

<hr>

## 3. For local development:

If you don't want to use the pre-built containers (i.e., you want to make custom code changes and build your own containers), then use the following instructions.

### Setup command aliases:

A script called `build_module.sh` is provided to expedite image assembly.  Setup command aliases to run it to build the required images after you make code changes:

```
alias ng-rss='./build_module.sh newsgears-rss'
```

#### Alternately, setup aliases build debuggable containers:

```
alias ng-rss='./build_module.sh newsgears-rss --debug 65005'
```

*Debuggable containers pause on startup until a remote debugger is attached on the specified port.*

### Build and run:

#### Run the following command in the directory that contains ```newsgears-rss```:

```
ng-rss && docker ...
```

Boot down in the regular way, by using ```docker ...``` in the ```newsgears-rss``` directory.

<hr> 

You can also use the `ng-rss` alias to rebuild the container (i.e., to deploy code changes).

```
$ ng-rss # rebuild the feed server container 
```
