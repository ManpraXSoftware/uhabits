create table Logs (
    id integer primary key autoincrement,
    repetition_id integer references Repetitions(id),
    value integer,
    timestamp integer
);