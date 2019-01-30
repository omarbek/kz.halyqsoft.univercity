delete from load_to_chair;
create unique index idx_load_to_chair
  on load_to_chair (
                    chair_id asc,
                    semester_id asc,
                    subject_id asc,
                    stream_id asc
    );