create table cart_cart_items
(
  cart_id      bigint not null,
  cart_item_id bigint not null,
  primary key (cart_id, cart_item_id)
)
  engine = MyISAM;

create table cart_items
(
  cart_item_id bigint not null
    primary key,
  product_id   bigint null,
  quantity     int    null
)
  engine = MyISAM;

create table carts
(
  cart_id int          not null
    primary key,
  owner   varchar(255) null,
  status  varchar(255) null
)
  engine = MyISAM;

create table orders
(
  order_id bigint       not null
    primary key,
  cart     bigint       null,
  status   int          null,
  owner    varchar(255) null,
  total    float        not null
)
  engine = MyISAM;

create table products
(
  id           bigint       not null
    primary key,
  category     varchar(255) null,
  description  varchar(255) null,
  manufacturer varchar(255) null,
  name         varchar(255) null,
  price        float        null,
  seller       varchar(255) null
)
  engine = MyISAM;

create table roles
(
  id   bigint      not null
    primary key,
  name varchar(60) null,
    unique (name)
)
  engine = MyISAM;

create table stock
(
  id         bigint not null
    primary key,
  product_id bigint null,
  quantity   bigint null
)
  engine = MyISAM;

create table user_roles
(
  user_id int    not null,
  role_id bigint not null,
  primary key (user_id, role_id)
)
  engine = MyISAM;

create table users
(
  user_id   int          not null
    primary key,
  email     varchar(255) null,
  last_name varchar(255) null,
  name      varchar(255) null,
  password  varchar(255) null,
  username  varchar(255) null
)
  engine = MyISAM;

