-- tables for JLiteBox
-- dlegaspi@bu.edu

drop table if exists camera;
drop table if exists lens;
drop table if exists image;
drop view if exists image_with_metadata;

-- camera information
create table camera
(
    id           text
        primary key,
    brand        text not null,
    model        text,
    is_autofocus integer default 0
);

-- lens information
create table lens
(
    id    text
        primary key,
    brand text not null,
    model text
);

-- the image
create table image
(
    name              text primary key,
    image_type        text default 'JPG',
    src_path          text not null,
    camera_id         text,
    lens_id           text,
    lens_focal_length real,
    shutter_speed     real,
    capture_date      integer,
    iso               integer,
    raw_metadata      text,
    image_preview     blob,
    foreign key (camera_id) references camera (id),
    foreign key (lens_id) references lens (id)
);

create view image_with_metadata as
select i.*,
       c.brand        as camera_brand,
       c.model        as camera_model,
       c.is_autofocus as camera_autofocus,
       l.brand        as lens_brand,
       l.model        as lens_model
from image i
         left join camera c on i.camera_id = c.id
         left join lens l on i.lens_id = l.id;


