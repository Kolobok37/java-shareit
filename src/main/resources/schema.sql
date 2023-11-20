create sequence IF NOT EXISTS hibernate_sequence start with 1 increment by 1;
create TABLE IF NOT EXISTS users (
user_id Long GENERATED BY DEFAULT AS IDENTITY NOT NULL,
name VARCHAR(255) NOT NULL,
email VARCHAR(512) NOT NULL,
CONSTRAINT pk_user PRIMARY KEY (user_id)
);

create TABLE IF NOT EXISTS items_booking(
booking_id Long,
item_id Long,
CONSTRAINT items_booking PRIMARY KEY (booking_id,item_ID)
);

create TABLE IF NOT EXISTS booking (
id Long GENERATED BY DEFAULT AS IDENTITY NOT NULL,
start_date datetime NOT NULL,
end_date datetime NOT NULL,
booker Long,
item_ID Long NOT NULL,
status VARCHAR(31),
CONSTRAINT pk_booking PRIMARY KEY (id)
);

create TABLE IF NOT EXISTS ITEMS_COMMENTS(
COMMENTS_id Long,
item_id Long,
CONSTRAINT ITEMS_COMMENTS PRIMARY KEY (COMMENTS_id,item_ID)
);

create TABLE IF NOT EXISTS comments (
id Long GENERATED BY DEFAULT AS IDENTITY NOT NULL,
booker Long,
item Long NOT NULL,
text VARCHAR(511),
date_comment datetime NOT NULL,
CONSTRAINT pk_reviews PRIMARY KEY (id)
);

create TABLE IF NOT EXISTS items (
id Long GENERATED BY DEFAULT AS IDENTITY NOT NULL,
OWNER_USER_ID Long NOT NULL,
name VARCHAR(255) NOT NULL,
description VARCHAR(512) NOT NULL,
available BOOLEAN NOT NULL,
next_booking_id long,
last_booking_id long,
CONSTRAINT pk_items PRIMARY KEY (id)
);
