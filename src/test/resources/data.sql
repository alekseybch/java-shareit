insert into users(name, email) values ('user', 'user@user.com');
insert into users(name, email) values ('other', 'other@other.com');
insert into users(name, email) values ('booker', 'booker@booker.com');
insert into users(name, email) values ('deleted', 'deleted@deleted.com');
insert into item_requests(description, requestor_id, created)
values ('need best juicer', 1, NOW());
insert into item_requests(description, requestor_id, created)
values ('need something', 2, NOW());
insert into items(name, description, is_available, owner_id, request_id)
values ('drill', 'simple drill', true, 1, null);
insert into items(name, description, is_available, owner_id, request_id)
values ('vacuum cleaner', 'simple vacuum cleaner', false, 2, null);
insert into items(name, description, is_available, owner_id, request_id)
values ('juicer', 'best juicer', true, 3, 1);
insert into bookings(start_date, end_date, item_id, booker_id, status)
values (NOW() + INTERVAL '1' DAY, NOW() + INTERVAL '7' DAY, 1, 3, 'APPROVED');
insert into bookings(start_date, end_date, item_id, booker_id, status)
values (NOW() - INTERVAL '3' DAY, NOW() - INTERVAL '2' DAY, 2, 1, 'APPROVED');
insert into bookings(start_date, end_date, item_id, booker_id, status)
values (NOW() + INTERVAL '1' DAY, NOW() + INTERVAL '7' DAY, 3, 2, 'WAITING');
insert into bookings(start_date, end_date, item_id, booker_id, status)
values (NOW() - INTERVAL '1' DAY, NOW() + INTERVAL '5' DAY, 2, 3, 'WAITING');
insert into bookings(start_date, end_date, item_id, booker_id, status)
values (NOW() - INTERVAL '8' DAY, NOW() - INTERVAL '1' DAY, 1, 2, 'APPROVED');
insert into comments(text, item_id, author_id, created)
values ('works', 1, 3, NOW() + INTERVAL '8' DAY);