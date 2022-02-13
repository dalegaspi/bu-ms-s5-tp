drop table if exists  image_metadata;
create table image_metadata
(
    name              text primary key,
    image_type        text default 'JPG',
    src_path          text not null,
    camera_brand      text,
    camera_model      text,
    camera_autofocus  numeric,
    lens_brand        text,
    lens_model        text,
    lens_focal_length real,
    shutter_speed     real,
    capture_date      integer,
    iso               integer,
    raw_metadata      text,
    image_preview     blob
);
