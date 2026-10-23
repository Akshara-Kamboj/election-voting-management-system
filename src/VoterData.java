import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * VoterData.java
 * Shared TEMPORARY storage for registered voters.
 *
 * Every screen (VoterRegistration, VoterLogin, VoterDashboard) uses the
 * same static list below, so a voter registered on one screen can be
 * found by the others while the program is running.
 *
 * IMPORTANT: this data lives only in memory. It is lost when the
 * application closes. A database or file will replace it later.
 *
 * No package declaration - keep all .java files in the same "src" folder.
 */
public class VoterData {

    // =====================================================================
    // Voter - one registered voter
    // =====================================================================
    public static class Voter {
        private final String fullName;
        private final LocalDate dateOfBirth;
        private final String aadhaarNumber;  // never shown in full - see getMaskedAadhaar()
        private final String voterId;
        private final String username;
        private final String password;   // plain text: prototype only!
        private boolean hasVoted;        // for the future voting screen

        public Voter(String fullName, LocalDate dateOfBirth, String aadhaarNumber,
                     String voterId, String username, String password) {
            this.fullName = fullName;
            this.dateOfBirth = dateOfBirth;
            this.aadhaarNumber = aadhaarNumber;
            this.voterId = voterId;
            this.username = username;
            this.password = password;
            this.hasVoted = false;       // nobody has voted yet
        }

        public String getFullName() { return fullName; }
        public LocalDate getDateOfBirth() { return dateOfBirth; }
        public String getVoterId()  { return voterId; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public boolean hasVoted()   { return hasVoted; }

        /**
         * Aadhaar number with only the last 4 digits visible, e.g.
         * "XXXX-XXXX-1234". Use this if an Aadhaar number must ever be shown.
         * There is deliberately no getter for the full number.
         */
        public String getMaskedAadhaar() {
            String last4 = aadhaarNumber.substring(aadhaarNumber.length() - 4);
            return "XXXX-XXXX-" + last4;
        }

        /** True if this voter's Aadhaar number equals the given one. */
        public boolean hasAadhaar(String number) {
            return aadhaarNumber.equals(number);
        }

        public void setHasVoted(boolean hasVoted) { this.hasVoted = hasVoted; }
    }

    // =====================================================================
    // The shared list (static = one copy for the whole program)
    // =====================================================================
    private static final List<Voter> voters = new ArrayList<>();

    // Private constructor: this class is only used through its static methods
    private VoterData() { }

    /** Adds a new voter. Check for duplicates BEFORE calling this. */
    public static void addVoter(Voter voter) {
        voters.add(voter);
    }

    /** True if a voter with this Voter ID already exists (ignores upper/lower case). */
    public static boolean voterIdExists(String voterId) {
        for (Voter v : voters) {
            if (v.getVoterId().equalsIgnoreCase(voterId)) {
                return true;
            }
        }
        return false;
    }

    /** True if a voter with this Aadhaar number is already registered. */
    public static boolean aadhaarExists(String aadhaarNumber) {
        for (Voter v : voters) {
            if (v.hasAadhaar(aadhaarNumber)) {
                return true;
            }
        }
        return false;
    }

    /** True if this username is already taken (ignores upper/lower case). */
    public static boolean usernameExists(String username) {
        for (Voter v : voters) {
            if (v.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * For VoterLogin: returns the matching Voter, or null if the
     * username/password combination is wrong.
     */
    public static Voter authenticate(String username, String password) {
        for (Voter v : voters) {
            if (v.getUsername().equalsIgnoreCase(username)
                    && v.getPassword().equals(password)) {
                return v;
            }
        }
        return null;
    }

    /** Read-only view of all voters (useful for the admin dashboard). */
    public static List<Voter> getAllVoters() {
        return Collections.unmodifiableList(voters);
    }

    /** How many voters are registered right now. */
    public static int getVoterCount() {
        return voters.size();
    }

    // =====================================================================
    // Currently logged-in voter (set by VoterLogin, read by VoterDashboard)
    // =====================================================================

    // null means "nobody is logged in"
    private static Voter currentVoter = null;

    /** Called by VoterLogin after a successful login. */
    public static void setCurrentVoter(Voter voter) {
        currentVoter = voter;
    }

    /** Returns the logged-in voter, or null if nobody is logged in. */
    public static Voter getCurrentVoter() {
        return currentVoter;
    }

    /** True if a voter is currently logged in. */
    public static boolean isLoggedIn() {
        return currentVoter != null;
    }

    /** Called on logout: forgets the current voter. */
    public static void logout() {
        currentVoter = null;
    }
}