# decorators
Useful decorators implemented using pure Java.

These decorators implement AOP in pure Java. No dependencies required.

See usage in [tests directory](src/test/java/com/github/scfj/decorators).

Decorators can wrap any objects, but *they can be cast only to interfaces*.

E. g.:

```java
// Loggable is a decorator and Identity is a class
Identity identity = Loggable.decorate(new Identity()); // WRONG
// ^ casting to class, not an interface.

// class LocalPictureStorage implements PictureStorage
PictureStorage storage = Cached.decorate(new LocalPictureStorage()); // OK
// ^ casting to interface.
```

It means that only methods declared in interfaces can be wrapped by decorators.\
And it is good since it encourages you not to have methods uncovered with interfaces.

## Basic usage:
```java
Interface object = Decorator.decorate(target, args);
object.method1(); // decorator will handle that
```

## Concrete examples:
### Cached
Cached decorator caches methods' calls to target. Works well\
with immutable objects and arguments (they have constant hashCode).
```java
import com.github.scfj.decorators.Cached;

// ...
UserApi userApi = Cached.decorate(new UsersFromDatabase("jdbc://..."));

// Retreives user from database, interacts with IO for the first time.
User firstUser = userApi.userById(1);
// ...
User user1 = userApi.userById(1); // No IO, user is stored in memory.
```
