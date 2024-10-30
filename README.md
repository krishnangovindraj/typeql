[![TypeQL](./banner.png)](https://typedb.com/docs/typeql/2.x/overview)

[![Factory](https://factory.vaticle.com/api/status/typedb/typeql/badge.svg)](https://factory.vaticle.com/typedb/typeql)
[![GitHub release](https://img.shields.io/github/release/typedb/typeql.svg)](https://github.com/typedb/typeql/releases/latest)
[![Discord](https://img.shields.io/discord/665254494820368395?color=7389D8&label=chat&logo=discord&logoColor=ffffff)](https://typedb.com/discord)
[![Discussion Forum](https://img.shields.io/badge/discourse-forum-blue.svg)](https://forum.typedb.com)
[![Stack Overflow](https://img.shields.io/badge/stackoverflow-typedb-796de3.svg)](https://stackoverflow.com/questions/tagged/typedb)
[![Stack Overflow](https://img.shields.io/badge/stackoverflow-typeql-3dce8c.svg)](https://stackoverflow.com/questions/tagged/typeql)
[![Hosted By: Cloudsmith](https://img.shields.io/badge/OSS%20hosting%20by-cloudsmith-blue?logo=cloudsmith&style=flat)](https://cloudsmith.com)

# Introducing TypeQL

TypeQL is the query language of **[TypeDB](https://github.com/typedb/typedb)**.

- **Conceptual and intuitive**. TypeQL is based directly on the [conceptual data model](https://development.typedb.com/philosophy) of TypeDB. Its queries comprise sequences of statements that assemble into [patterns](https://development.typedb.com/features#modern-language). This mirrors natural language and makes it easy and intuitive to express even highly complex queries.
- **Fully declarative and composable** TypeQL is fully declarative, allowing us to define query patterns without considering execution strategy. The user only composes sets of requirements, and TypeDB finds all matching data to process. 
- **A fully variablizable language**. Any concept in TypeQL has a type, and so any concept in TypeQL can be variablized in a query – even types! This enables TypeQL to express powerful [parametric](https://typedb.com/features#polymorphic-queries) database operations.
- **Built for consistency**. TypeQL patterns are underpinned by a powerful type system that ensure safety and consistency of database applications.

For a quick overview of the range of statements that are available in TypeQL check out our [TypeQL in 20 queries guide](https://typedb.com/docs/).

> **IMPORTANT NOTE:** > > TypeDB & TypeQL are in the process of being rewritten in [Rust](https://www.rust-lang.org). There will be significant refinement to the language, and minor breaks in backwards compatibility. Learn about the changes on our [roadmap issue on GitHub](https://github.com/typedb/typedb/issues/6764). The biggest change to TypeDB 3.0 will be our storage data structure and architecture that significantly boosts performance. We’re aiming to release 3.0 in the summer this year, along with preliminary benchmarks of TypeDB.

## A polymorphic query language

### Define types, inheritance, and interfaces

TypeQL features the type system of the [Polymorphic Entity-Relation-Attribute](https://typedb.com/philosophy) (PERA) model: entities are independent concepts, relations depend on role interfaces played by either entities or relations, and attributes are properties with a value that can interface with (namely, be owned by) entities or relations. Entities, relations, and attributes are all considered first-class citizens and can be subtyped, allowing for expressive modeling without the need for normalization or reification.

```php
define

id sub attribute, value string;
email sub id;
path sub id;
name sub id;

user sub entity,
    owns email @unique,
    plays permission:subject,
    plays request:requestee;
file sub entity,
    owns path,
    plays permission:object;
action sub entity,
    owns name,
    plays permission:action;

permission sub relation,
    relates subject,
    relates object,
    relates action,
    plays request:target;
request sub relation,
    relates target,
    relates requestee;
```


### Write polymorphic database queries 

Use subtyping to query a common supertype and automatically retrieve matching data. Variablize queries to return types, roles, and data. New types added to the schema are automatically included in the results of pre-existing queries against their supertype, so no refactoring is necessary.

```
match $user isa user,
    has full-name $name,
    has email $email;
# This returns all users of any type

match $user isa employee,
    has full-name $name,
    has email $email,
    has employee-id $id;
# This returns only users who are employees

match $user-type sub user;
$user isa $user-type,
    has full-name $name,
    has email $email;
# This returns all users and their type
```


## Building queries with ease

### Gain clarity through natural and fully declarative syntax

TypeQL's near-natural syntax and fully declarative properties make queries easily understandable, reducing the learning curve and easing maintenance. This allows you to define query patterns without considering execution strategy. TypeDB's query planner always optimizes queries, so you don't have to worry about the logical implementation.

```php
match
$kevin isa user, has email "kevin@typedb.com";

insert
$chloe isa full-time-employee,
    has full-name "Chloé Dupond",
    has email "chloe@typedb.com",
    has employee-id 185,
    has weekly-hours 35;
$hire (employee: $chloe, ceo: $kevin) isa hiring,
    has date 2023-09-27;
```

### Develop modularly with fully composable query patterns

TypeDB's TypeQL query language uses pattern matching to find data. Patterns in TypeQL are fully composable. Every complex pattern can be broken down into a conjunction of atomic constraints, which can be concatenated in any order. Any pattern composed of valid constraints is guaranteed to be valid itself, no matter how complex.

```php
match 
$user isa user;

match
$user isa user;
$user has email "john@typedb.com";

match
$user isa user;
$user has email "john@typedb.com";
(team: $team, member: $user) isa team-membership;

match
$user isa user;
$user has email "john@typedb.com";
(team: $team, member: $user) isa team-membership;
$team has name "Engineering";
```


## TypeQL grammar

> Note: All TypeDB Clients, as well as TypeDB Console, accept TypeQL syntax natively. 
> If you are using TypeDB, you do not need additional libraries/tools to use TypeQL syntax natively.
> However, if you would like to construct TypeQL queries programmatically, you can do so with "Language Libraries" listed below.

- [TypeQL Grammar](https://github.com/typedb/typeql/blob/master/grammar/README.md)
- [TypeQL Language Library for Java](https://github.com/typedb/typeql/blob/master/java)
- [TypeQL Language Library for Rust (under development)](https://github.com/typedb/typeql/blob/master/rust)
- [TypeQL Language Library for Python (under development)](https://github.com/typedb-osi/typeql-lang-python)


## Resources

### Developer resources

- Documentation: https://typedb.com/docs
- Discussion Forum: https://forum.typedb.com/
- Discord Chat Server: https://typedb.com/discord
- Community Projects: https://github.com/typedb-osi

### Useful links

If you want to begin your journey with TypeDB, you can explore the following resources:

* More on TypeDB's [features](https://typedb.com/features)
* In-depth dive into TypeDB's [philosophy](https://typedb.com/philosophy)
* Our [TypeDB quickstart](https://typedb.com/docs/typedb/2.x/quickstart-guide)

## Contributions

TypeDB and TypeQL are built using various open-source frameworks and technologies throughout its evolution. 
Today TypeDB and TypeQL use
[Speedb](https://www.speedb.io/),
[pest](https://pest.rs/),
[SCIP](https://www.scipopt.org),
[Bazel](https://bazel.build),
[gRPC](https://grpc.io),
[ZeroMQ](https://zeromq.org), 
and [Caffeine](https://github.com/ben-manes/caffeine). 

Thank you!

In the past, TypeDB was enabled by various open-source products and communities that we are hugely thankful to:
[RocksDB](https://rocksdb.org),
[ANTLR](https://www.antlr.org),
[Apache Cassandra](http://cassandra.apache.org), 
[Apache Hadoop](https://hadoop.apache.org), 
[Apache Spark](http://spark.apache.org), 
[Apache TinkerPop](http://tinkerpop.apache.org), 
and [JanusGraph](http://janusgraph.org). 

### Package hosting
Package repository hosting is graciously provided by  [Cloudsmith](https://cloudsmith.com).
Cloudsmith is the only fully hosted, cloud-native, universal package management solution, that
enables your organization to create, store and share packages in any format, to any place, with total
confidence.

## Licensing

TypeQL grammar and language libraries are provided under the Mozilla Public License 2.0 (MPL 2.0),
and therefore freely usable without restriction when unmodified.

The full license can be founder at: [LICENSE](https://github.com/typedb/typeql/blob/master/LICENSE).
