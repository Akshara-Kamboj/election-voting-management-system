import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * VotingData.java
 * Shared TEMPORARY storage for the election itself.
 *
 * It holds:
 *   - the list of candidates
 *   - the election status (Not Started / Running / Ended)
 *   - the vote records
 *
 * REGISTERED VOTERS are NOT copied here. They stay in VoterData (the class
 * VoterRegistration and VoterLogin already use). VotingData simply asks
 * VoterData for them, so there is still only ONE voter list in the program.
 *
 * Every screen (AdminDashboard, ResultsPage, and your future voting screen)
 * uses the same static data below, so they always see the same election.
 *
 * IMPORTANT: this data lives only in memory. It is lost when the
 * application closes.
 *
 * No package declaration - keep all .java files in the same "src" folder.
 */
public class VotingData {

    // =====================================================================
    // Candidate - one person standing in the election
    // =====================================================================
    public static class Candidate {
        private final String id;
        private final String name;
        private final String party;

        public Candidate(String id, String name, String party) {
            this.id = id;
            this.name = name;
            this.party = party;
        }

        public String getId()    { return id; }
        public String getName()  { return name; }
        public String getParty() { return party; }
    }

    // =====================================================================
    // Vote - one vote record (who voted, and for which candidate)
    // =====================================================================
    public static class Vote {
        private final String voterId;
        private final String candidateId;

        public Vote(String voterId, String candidateId) {
            this.voterId = voterId;
            this.candidateId = candidateId;
        }

        public String getVoterId()     { return voterId; }
        public String getCandidateId() { return candidateId; }
    }

    // =====================================================================
    // Status - the three stages of the election
    // =====================================================================
    public enum Status {
        NOT_STARTED("Not Started"),
        RUNNING("Running"),
        ENDED("Ended");

        private final String label;   // text shown on screen

        Status(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    // =====================================================================
    // The shared data (static = one copy for the whole program)
    // =====================================================================

    /** An election needs at least this many candidates before it can start. */
    public static final int MIN_CANDIDATES = 2;

    private static final List<Candidate> candidates = new ArrayList<>();
    private static final List<Vote> votes = new ArrayList<>();
    private static Status status = Status.NOT_STARTED;

    // Private constructor: this class is only used through its static methods
    private VotingData() { }

    // =====================================================================
    // CANDIDATES
    // =====================================================================

    /**
     * Adds a candidate. Returns false (and adds nothing) if the election has
     * already started or the ID is already used.
     */
    public static boolean addCandidate(Candidate candidate) {
        if (status != Status.NOT_STARTED || candidateIdExists(candidate.getId())) {
            return false;
        }
        candidates.add(candidate);
        return true;
    }

    /**
     * Removes the candidate with this ID. Returns false if the election has
     * already started or no such candidate exists.
     */
    public static boolean removeCandidate(String id) {
        if (status != Status.NOT_STARTED) {
            return false;
        }
        Candidate found = findCandidate(id);
        if (found == null) {
            return false;
        }
        candidates.remove(found);
        return true;
    }

    /** True if a candidate with this ID exists (ignores upper/lower case). */
    public static boolean candidateIdExists(String id) {
        return findCandidate(id) != null;
    }

    /** Returns the candidate with this ID, or null if there is none. */
    public static Candidate findCandidate(String id) {
        for (Candidate c : candidates) {
            if (c.getId().equalsIgnoreCase(id)) {
                return c;
            }
        }
        return null;
    }

    /** Read-only view of all candidates, in the order they were added. */
    public static List<Candidate> getCandidates() {
        return Collections.unmodifiableList(candidates);
    }

    public static int getCandidateCount() {
        return candidates.size();
    }

    // =====================================================================
    // ELECTION STATUS
    // =====================================================================

    public static Status getStatus() {
        return status;
    }

    public static boolean isElectionRunning() {
        return status == Status.RUNNING;
    }

    /**
     * Starts the election. Returns false if it is not in the "Not Started"
     * stage or there are too few candidates.
     */
    public static boolean startElection() {
        if (status != Status.NOT_STARTED || candidates.size() < MIN_CANDIDATES) {
            return false;
        }
        status = Status.RUNNING;
        return true;
    }

    /** Ends the election. Returns false if it is not currently running. */
    public static boolean endElection() {
        if (status != Status.RUNNING) {
            return false;
        }
        status = Status.ENDED;
        return true;
    }

    // =====================================================================
    // REGISTERED VOTERS (kept in VoterData - not duplicated here)
    // =====================================================================

    /** Number of registered voters, read from the shared VoterData class. */
    public static int getRegisteredVoterCount() {
        return VoterData.getVoterCount();
    }

    // =====================================================================
    // VOTES
    // =====================================================================

    /**
     * Records one vote. Your future voting screen should call this.
     *
     * @return null if the vote was recorded, otherwise a message explaining
     *         why it was refused (show it to the voter).
     */
    public static String castVote(VoterData.Voter voter, String candidateId) {
        if (voter == null) {
            return "No voter is logged in.";
        }
        if (status != Status.RUNNING) {
            return "Voting is not open right now.";
        }
        if (voter.hasVoted() || hasVoterVoted(voter.getVoterId())) {
            return "You have already voted.";
        }
        if (findCandidate(candidateId) == null) {
            return "That candidate does not exist.";
        }

        votes.add(new Vote(voter.getVoterId(), candidateId));
        voter.setHasVoted(true);   // so VoterDashboard shows "Already voted"
        return null;               // null = success
    }

    /** True if this Voter ID already has a vote record. */
    public static boolean hasVoterVoted(String voterId) {
        for (Vote v : votes) {
            if (v.getVoterId().equalsIgnoreCase(voterId)) {
                return true;
            }
        }
        return false;
    }

    /** Total number of votes cast so far. */
    public static int getTotalVotes() {
        return votes.size();
    }

    /** Number of votes received by one candidate. */
    public static int getVotesFor(String candidateId) {
        int count = 0;
        for (Vote v : votes) {
            if (v.getCandidateId().equalsIgnoreCase(candidateId)) {
                count++;
            }
        }
        return count;
    }

    /** Read-only view of all vote records. */
    public static List<Vote> getVoteRecords() {
        return Collections.unmodifiableList(votes);
    }
}