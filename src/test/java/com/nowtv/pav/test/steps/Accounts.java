package com.nowtv.pav.test.steps;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.generators.core.RandomFromCollectionGenerator;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Accounts {
	
    private List<String> accountIds;

    public String getRandomAccount() {
        return new RandomFromCollectionGenerator<>(accountIds).next();
    }

    public Collection<Long> allAccounts() {
        return accountIds.stream().map(Long::parseLong).collect(toList());
    }
}
