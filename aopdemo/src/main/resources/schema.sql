drop table if exists log_message;

create table log_message
(
	id bigint auto_increment,
	username varchar(50) null,
	operation varchar(50) null,
	time numeric(11) null,
	method varchar(100) null,
	params varchar(200) null,
	ip varchar(64) null,
	create_time date null,
	constraint log_message_pk
		primary key (id)
);