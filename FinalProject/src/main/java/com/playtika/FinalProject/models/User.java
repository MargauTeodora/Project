package com.playtika.FinalProject.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.playtika.FinalProject.exceptions.GameSessionException;
import com.playtika.FinalProject.exceptions.UserException;
import com.playtika.FinalProject.exceptions.customErrors.UserErrorCode;
import com.playtika.FinalProject.models.dto.SignUpRequest;
import com.playtika.FinalProject.utils.CustomTime;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Table(name = "users")
public class User {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;
    @Column()
    private boolean isPlaying;

    @Transient
    private boolean isExceedingDailyPlayTime;

    public boolean isExceedingDailyPlayTime() {
        return isExceedingDailyPlayTime;
    }

    public void setExceedingDailyPlayTime(boolean exceedingDailyPlayTime) {
        isExceedingDailyPlayTime = exceedingDailyPlayTime;
    }

    @Embedded
    @Column(name = "maximum_daily_play_time")
    private CustomTime maximumDailyPlayTime = new CustomTime(0, 0);

    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
    private List<GameSession> gameSessions;

    public List<GameSession> getGameSessions() {
        return gameSessions;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public void setGameSessions(List<GameSession> gameSessions) {
        this.gameSessions = gameSessions;
    }

    public void addGameSessions(GameSession gameSession) {
        if (this.gameSessions == null) {
            this.gameSessions = Arrays.asList(gameSession);
        } else {
            this.gameSessions.add(gameSession);
        }
    }


    public CustomTime getMaximumDailyPlayTime() {
        return maximumDailyPlayTime;
    }

    public User setMaximumDailyPlayTime(CustomTime maximumDailyPlayTime) {
        verifyMaxDailyPlayTime(maximumDailyPlayTime);
        this.maximumDailyPlayTime = maximumDailyPlayTime;
        return this;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        validateUsername(username);
        this.username = username;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public User setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        validateEmail(email);
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        validatePassword(password);
        this.password = password;
        return this;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public User setRoles(List<Role> roles) {

        this.roles = roles;
        return this;
    }

    public boolean isAdmin() {
        return roles.contains(new Role(RoleType.ROLE_ADMIN.name()));
    }

    public boolean isManager() {
        return roles.contains(new Role(RoleType.ROLE_MANAGER.name()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    private void hasCredentialValid(SignUpRequest request) {

    }

    private void validateUsername(String email) {
        String regex = "^[a-zA-Z]{4,}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        if (isEmptyString(email) || !matcher.find()) {
            throw new UserException(UserErrorCode.INCORRECT_USERNAME);
        }
    }


    private void validateEmail(String email) {
        String regex = "\\S+@\\S+\\.\\S+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        if (isEmptyString(email) || !matcher.find()) {
            throw new UserException(UserErrorCode.INCORRECT_EMAIL);
        }
    }

    private void validatePassword(String password) {
        String regex = "[a-zA-Z]{3,}[1-9]*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        if (isEmptyString(password) || !matcher.find()) {
            throw new UserException(UserErrorCode.INCORRECT_PASSWORD);
        }
    }

    private boolean isEmptyString(String string) {
        return string == null
                || string.isBlank();
    }

    private void verifyMaxDailyPlayTime(CustomTime request) {
        if (request != null) {
            if (request.getHour() < 0 || request.getMinutes() < 0) {
                throw new GameSessionException(GameSessionException.GameSessionErrorCode.NEGATIVE_NUMBER);
            }
            if (request.getHour() > 23) {
                throw new GameSessionException(GameSessionException.GameSessionErrorCode.EXCEED_DAILY_HOURS);
            }
            if (request.getMinutes() > 59) {
                throw new GameSessionException(GameSessionException.GameSessionErrorCode.EXCEED_MINUTES);
            }
        }
    }

    public CustomTime getPlayedTime() {
        int total = 0;
        for (GameSession gameSession : gameSessions) {
            LocalDateTime startDay = convertToLocalDateTime(gameSession.getStartDate());
            LocalDateTime endDate = convertToLocalDateTime(new Date());
            if (ChronoUnit.DAYS.between(startDay, endDate) != 0) {
                continue;
            }
            total += gameSession.getDuration().getHour() + gameSession.getDuration().getMinutes();
        }
        int hour = total / 60;
        int minutes = total % 60;

        return new CustomTime(hour, minutes);
    }

    private LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
