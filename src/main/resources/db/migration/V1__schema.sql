CREATE TABLE data_set (
  id UUID PRIMARY KEY,
  created DATE
);

CREATE TABLE vote_session (
  id UUID PRIMARY KEY,
  session_number INTEGER,
  session_date DATE,
  fk_data_set_id UUID REFERENCES data_set
);

CREATE TABLE vote_results (
  id UUID PRIMARY KEY,
  topic TEXT,
  meta_data VARCHAR(255),
  fk_vote_session_id UUID REFERENCES vote_session
);

CREATE TABLE delegate (
  id UUID PRIMARY KEY,

  first_name VARCHAR(255),
  middle_name VARCHAR(255),
  last_name VARCHAR(255)
);

CREATE TABLE vote(
  id UUID PRIMARY KEY,
  result VARCHAR(255),
  fk_delegate_id UUID REFERENCES delegate,
  fk_voting_results_id UUID REFERENCES vote_results
)
