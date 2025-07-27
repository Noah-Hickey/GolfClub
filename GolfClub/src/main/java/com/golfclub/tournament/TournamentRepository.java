package com.golfclub.tournament;

import java.time.LocalDate;

public interface TournamentRepository {

    public Tournament findByStartDate(LocalDate startDate);
    public Tournament findByLocationContainingIgnoreCase(String location);
}
