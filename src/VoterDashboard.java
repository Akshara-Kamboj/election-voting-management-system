import java.awt.*;
import java.awt.event.*;

/**
 * VoterDashboard.java
 * Election Voting Mechanism System - Voter Dashboard (Java AWT only).
 *
 * All data comes from the shared classes (this screen keeps no data of its own):
 *   VoterData  - the logged-in voter (set by VoterLogin)
 *   VotingData - candidates, election status and vote records (the same
 *                data that AdminDashboard changes)
 *
 * Cast Vote  -> VotingData.castVote(...) records the vote and marks the voter as voted
 * Logout     -> returns to MainMenu
 *
 * No package declaration - keep all .java files in the same "src" folder.
 */
public class VoterDashboard extends Frame implements ActionListener {

    // ---------- Theme colours (same as the other screens) ----------
    private static final Color NAVY       = new Color(10, 36, 99);
    private static final Color GREEN      = new Color(40, 167, 69);
    private static final Color RED        = new Color(200, 35, 51);
    private static final Color LIGHT_GREY = new Color(238, 240, 243);
    private static final Color WHITE      = Color.WHITE;
    private static final Color TEXT_GREY  = new Color(90, 90, 90);

    // ---------- Theme fonts ----------
    private static final Font TITLE_FONT   = new Font("SansSerif", Font.BOLD, 26);
    private static final Font HEADING_FONT = new Font("SansSerif", Font.BOLD, 18);
    private static final Font LABEL_FONT   = new Font("SansSerif", Font.BOLD, 14);
    private static final Font TEXT_FONT    = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font BUTTON_FONT  = new Font("SansSerif", Font.BOLD, 14);
    private static final Font SMALL_FONT   = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font CHOICE_FONT  = new Font("SansSerif", Font.PLAIN, 16);

    // ---------- Duplicate-window protection ----------
    // Holds the one open VoterDashboard window (or null when none is open).
    private static VoterDashboard openInstance = null;

    // The voter who logged in (null if this screen was opened without a login)
    private VoterData.Voter voter;

    // ---------- Components ----------
    private Label statusValue;          // "Not Started" / "Running" / "Ended"
    private Label statusMessage;        // explains what the status means for the voter
    private Label votingStatusLabel;    // "Your voting status: ..."

    // One Checkbox per candidate. Putting them in a CheckboxGroup makes them
    // behave like radio buttons: exactly one can be selected at a time.
    private CheckboxGroup candidateGroup;
    private Checkbox[] candidateBoxes;

    private Button castButton;
    private Button checkButton;
    private Button logoutButton;

    // True once we are leaving this screen; ignores extra clicks.
    private boolean leaving = false;

    /** Builds and shows the Voter Dashboard window. */
    public VoterDashboard() {
        super("Voter Dashboard");

        // If a dashboard is already open, bring it to the front
        // and do not create a second one.
        if (openInstance != null) {
            openInstance.toFront();
            dispose();
            return;
        }
        openInstance = this;

        // Who is logged in? VoterLogin stored this in the shared VoterData class.
        voter = VoterData.getCurrentVoter();

        setSize(720, 700);
        setResizable(false);
        setBackground(LIGHT_GREY);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createBodyPanel(), BorderLayout.CENTER);

        // Clicking the window's X button behaves like Logout
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logout();
            }
        });

        // Centre the window on the screen
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth()) / 2,
                    (screen.height - getHeight()) / 2);

        refreshStatus();    // fill in the status texts from the shared data
        setVisible(true);
    }

    /** Frees the "one window only" slot whenever this window is closed. */
    @Override
    public void dispose() {
        if (openInstance == this) {
            openInstance = null;
        }
        super.dispose();
    }

    // =====================================================================
    // SMALL HELPER PANELS
    // =====================================================================

    /** A Panel with inner padding (top, left, bottom, right). */
    private static class PaddedPanel extends Panel {
        private final Insets padding;

        PaddedPanel(LayoutManager layout, int top, int left, int bottom, int right) {
            super(layout);
            padding = new Insets(top, left, bottom, right);
        }

        @Override
        public Insets getInsets() {
            return padding;
        }
    }

    /** A thin coloured strip (used as an accent line). */
    private Panel createStrip(Color color, final int height) {
        Panel strip = new Panel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, height);
            }
        };
        strip.setBackground(color);
        return strip;
    }

    /** Creates a Label with the given font and colour. */
    private Label createLabel(String text, int alignment, Font font, Color color) {
        Label label = new Label(text, alignment);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    // =====================================================================
    // UI BUILDING METHODS
    // =====================================================================

    /** Navy header: heading, subtitle and a thin green accent line. */
    private Panel createHeaderPanel() {
        Panel header = new PaddedPanel(new GridLayout(2, 1, 0, 4), 20, 10, 16, 10);
        header.setBackground(NAVY);

        header.add(createLabel("Election Voting Mechanism System", Label.CENTER, TITLE_FONT, WHITE));
        header.add(createLabel("Voter Dashboard", Label.CENTER,
                new Font("SansSerif", Font.PLAIN, 16), new Color(144, 238, 144)));

        Panel wrapper = new Panel(new BorderLayout());
        wrapper.add(header, BorderLayout.CENTER);
        wrapper.add(createStrip(GREEN, 5), BorderLayout.SOUTH);
        return wrapper;
    }

    /** Everything under the header. */
    private Panel createBodyPanel() {
        Panel body = new PaddedPanel(new BorderLayout(0, 12), 18, 25, 18, 25);
        body.setBackground(LIGHT_GREY);

        // Welcome card and election status card stacked at the top
        Panel top = new Panel(new BorderLayout(0, 12));
        top.setBackground(LIGHT_GREY);
        top.add(createWelcomeCard(), BorderLayout.NORTH);
        top.add(createStatusCard(), BorderLayout.CENTER);

        body.add(top, BorderLayout.NORTH);
        body.add(createCandidatesCard(), BorderLayout.CENTER);
        body.add(createButtonRow(), BorderLayout.SOUTH);
        return body;
    }

    /** White card with the welcome message and the voter's ID. */
    private Panel createWelcomeCard() {
        Panel card = new PaddedPanel(new GridLayout(2, 1, 0, 2), 12, 20, 12, 20);
        card.setBackground(WHITE);

        if (voter == null) {
            card.add(createLabel("No voter is logged in", Label.LEFT, HEADING_FONT, RED));
            card.add(createLabel("Please log in from the main menu.", Label.LEFT, TEXT_FONT, TEXT_GREY));
        } else {
            card.add(createLabel("Welcome, " + voter.getFullName(), Label.LEFT, HEADING_FONT, NAVY));
            card.add(createLabel("Voter ID: " + voter.getVoterId(), Label.LEFT, TEXT_FONT, TEXT_GREY));
        }
        return card;
    }

    /** White card showing the election status and the voter's own voting status. */
    private Panel createStatusCard() {
        Panel card = new PaddedPanel(new GridLayout(4, 1, 0, 3), 12, 20, 12, 20);
        card.setBackground(WHITE);

        statusValue       = createLabel("", Label.LEFT, new Font("SansSerif", Font.BOLD, 20), NAVY);
        statusMessage     = createLabel("", Label.LEFT, TEXT_FONT, TEXT_GREY);
        votingStatusLabel = createLabel("", Label.LEFT, LABEL_FONT, NAVY);

        card.add(createLabel("Election Status", Label.LEFT, LABEL_FONT, TEXT_GREY));
        card.add(statusValue);
        card.add(statusMessage);
        card.add(votingStatusLabel);
        return card;
    }

    /** White card with one radio-style Checkbox per candidate. */
    private Panel createCandidatesCard() {
        Panel card = new PaddedPanel(new BorderLayout(0, 8), 14, 20, 14, 20);
        card.setBackground(WHITE);

        Panel top = new Panel(new GridLayout(2, 1, 0, 2));
        top.setBackground(WHITE);
        top.add(createLabel("Select Your Candidate", Label.LEFT, HEADING_FONT, NAVY));
        top.add(createLabel("Choose one candidate, then click Cast Vote.", Label.LEFT, SMALL_FONT, TEXT_GREY));
        card.add(top, BorderLayout.NORTH);

        // Read the candidates from the shared VotingData class
        int count = VotingData.getCandidateCount();
        candidateGroup = new CheckboxGroup();
        candidateBoxes = new Checkbox[count];

        if (count == 0) {
            card.add(createLabel("No candidates have been added yet.", Label.CENTER, LABEL_FONT, RED),
                    BorderLayout.CENTER);
            return card;
        }

        Panel choices = new PaddedPanel(new GridLayout(count, 1, 0, 6), 6, 4, 6, 4);
        choices.setBackground(WHITE);

        int i = 0;
        for (VotingData.Candidate c : VotingData.getCandidates()) {
            // Passing the CheckboxGroup makes this a radio button
            Checkbox box = new Checkbox(c.getName() + "   -   " + c.getParty(), candidateGroup, false);
            box.setFont(CHOICE_FONT);
            box.setBackground(WHITE);
            box.setForeground(NAVY);
            candidateBoxes[i] = box;   // same order as VotingData.getCandidates()
            choices.add(box);
            i++;
        }

        // A ScrollPane adds a scroll bar if there are too many candidates to fit
        ScrollPane scroll = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
        scroll.setBackground(WHITE);
        scroll.add(choices);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    /** Bottom row: Cast Vote, Check Voting Status, Logout. */
    private Panel createButtonRow() {
        castButton   = createButton("Cast Vote", GREEN);
        checkButton  = createButton("Check Voting Status", NAVY);
        logoutButton = createButton("Logout", TEXT_GREY);

        Panel row = new Panel(new GridLayout(1, 3, 12, 0));
        row.setBackground(LIGHT_GREY);
        row.add(castButton);
        row.add(checkButton);
        row.add(logoutButton);
        return row;
    }

    /** Creates one styled button and attaches the ActionListener. */
    private Button createButton(String text, Color background) {
        Button button = new Button(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(background);
        button.setForeground(WHITE);
        button.setPreferredSize(new Dimension(150, 44));
        button.addActionListener(this);
        return button;
    }

    // =====================================================================
    // SHOWING THE SHARED DATA
    // =====================================================================

    /** True if the logged-in voter has already voted. */
    private boolean hasVoted() {
        return voter != null
                && (voter.hasVoted() || VotingData.hasVoterVoted(voter.getVoterId()));
    }

    /** Re-reads the shared data and updates the status texts and the radio buttons. */
    private void refreshStatus() {
        VotingData.Status status = VotingData.getStatus();
        boolean voted = hasVoted();

        // ----- election status and what it means for the voter -----
        statusValue.setText(status.getLabel());

        if (status == VotingData.Status.RUNNING) {
            statusValue.setForeground(GREEN);
            if (voted) {
                statusMessage.setText("Voting is open, and you have already voted. Thank you!");
            } else {
                statusMessage.setText("Voting is open. Select one candidate and click Cast Vote.");
            }
        } else if (status == VotingData.Status.ENDED) {
            statusValue.setForeground(RED);
            statusMessage.setText("The election has ended. Voting is closed.");
        } else {
            statusValue.setForeground(NAVY);
            statusMessage.setText("The election has not started yet. Voting is not open.");
        }

        // ----- this voter's own status -----
        if (voted) {
            votingStatusLabel.setText("Your voting status: Already voted");
            votingStatusLabel.setForeground(GREEN);
        } else {
            votingStatusLabel.setText("Your voting status: Not voted yet");
            votingStatusLabel.setForeground(RED);
        }

        // The radio buttons can only be used while voting is possible
        boolean canVote = (voter != null) && status == VotingData.Status.RUNNING && !voted;
        for (Checkbox box : candidateBoxes) {
            box.setEnabled(canVote);
        }
    }

    // =====================================================================
    // BUTTON ACTIONS
    // =====================================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        if (leaving) {
            return; // already moving to another screen
        }

        Object source = e.getSource();

        if (source == castButton) {
            castVote();
        } else if (source == checkButton) {
            checkVotingStatus();
        } else if (source == logoutButton) {
            logout();
        }
    }

    /** Shows an error and returns false if nobody is logged in. */
    private boolean requireLogin() {
        if (voter == null) {
            showMessage("Not Logged In",
                    "No voter is logged in.\nPlease log in from the main menu.", RED);
            return false;
        }
        return true;
    }

    // ---------------------------------------------------------------------
    // Cast Vote
    // ---------------------------------------------------------------------

    private void castVote() {
        if (!requireLogin()) {
            return;
        }

        // Rule 1: there must be candidates
        if (VotingData.getCandidateCount() == 0) {
            showMessage("No Candidates",
                    "There are no candidates to vote for yet.\nPlease check again later.", RED);
            return;
        }

        // Rule 2: the election must be running
        VotingData.Status status = VotingData.getStatus();
        if (status == VotingData.Status.NOT_STARTED) {
            showMessage("Election Not Started",
                    "The election has not started yet.\nPlease wait for the administrator to start it.", RED);
            return;
        }
        if (status == VotingData.Status.ENDED) {
            showMessage("Election Ended",
                    "The election has ended.\nVoting is now closed.", RED);
            return;
        }

        // Rule 3: one vote per voter
        if (hasVoted()) {
            showMessage("Already Voted",
                    "You have already voted.\nEach voter can vote only once.", RED);
            return;
        }

        // Rule 4: exactly one candidate must be selected
        Checkbox selected = candidateGroup.getSelectedCheckbox();   // null = nothing chosen
        if (selected == null) {
            showMessage("No Candidate Selected",
                    "Please select one candidate\nbefore casting your vote.", RED);
            return;
        }

        // Find the candidate that belongs to the selected radio button.
        // The boxes are in the same order as VotingData.getCandidates().
        VotingData.Candidate candidate = null;
        for (int i = 0; i < candidateBoxes.length; i++) {
            if (candidateBoxes[i] == selected) {
                candidate = VotingData.getCandidates().get(i);
            }
        }
        if (candidate == null) {
            return; // should never happen
        }

        // Ask for confirmation before recording the vote
        boolean sure = confirm("Confirm Your Vote",
                "You are voting for:\n" + candidate.getName() + " (" + candidate.getParty() + ")\n"
                + "Your vote cannot be changed afterwards.");
        if (!sure) {
            return;
        }

        // Record the vote in the shared VotingData class.
        // It returns null on success, or a message explaining why it was refused.
        String problem = VotingData.castVote(voter, candidate.getId());
        if (problem != null) {
            refreshStatus();
            showMessage("Vote Not Recorded", problem, RED);
            return;
        }

        candidateGroup.setSelectedCheckbox(null);   // clear the choice from the screen
        refreshStatus();                            // now shows "Already voted"
        showMessage("Vote Recorded",
                "Your vote has been recorded.\nThank you for voting!", GREEN);
    }

    // ---------------------------------------------------------------------
    // Check Voting Status
    // ---------------------------------------------------------------------

    private void checkVotingStatus() {
        if (!requireLogin()) {
            return;
        }

        VotingData.Status status = VotingData.getStatus();

        if (hasVoted()) {
            showMessage("Voting Status",
                    "You have already voted.\nYour vote has been recorded.", GREEN);
        } else if (status == VotingData.Status.RUNNING) {
            showMessage("Voting Status",
                    "You have not voted yet.\nYou can still cast your vote.", NAVY);
        } else if (status == VotingData.Status.NOT_STARTED) {
            showMessage("Voting Status",
                    "You have not voted yet.\nThe election has not started.", NAVY);
        } else {
            showMessage("Voting Status",
                    "You did not vote in this election.\nVoting is now closed.", RED);
        }
    }

    // ---------------------------------------------------------------------
    // Logout
    // ---------------------------------------------------------------------

    /** Forgets the logged-in voter, closes this window and returns to the menu. */
    private void logout() {
        if (leaving) {
            return;
        }
        leaving = true;
        VoterData.logout();
        dispose();
        new MainMenu();
    }

    // =====================================================================
    // MESSAGE AND CONFIRMATION DIALOGS
    // =====================================================================

    /** Moves a dialog so it sits in the middle of the dashboard window. */
    private void centerOnFrame(Dialog dialog) {
        dialog.setLocation(getX() + (getWidth() - dialog.getWidth()) / 2,
                           getY() + (getHeight() - dialog.getHeight()) / 2);
    }

    /** A button for use inside dialogs. */
    private Button createDialogButton(String text, Color background) {
        Button button = new Button(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(background);
        button.setForeground(WHITE);
        button.setPreferredSize(new Dimension(110, 38));
        return button;
    }

    /** A panel with one Label per line of the message ("\n" = new line). */
    private Panel createMessagePanel(String message, Color color) {
        String[] lines = message.split("\n");
        Panel panel = new Panel(new GridLayout(lines.length, 1));
        panel.setBackground(WHITE);
        for (String line : lines) {
            panel.add(createLabel(line, Label.CENTER, new Font("SansSerif", Font.BOLD, 15), color));
        }
        return panel;
    }

    /**
     * AWT has no ready-made message box, so we build a small modal Dialog.
     * "Modal" means the dashboard cannot be used until it is closed.
     *
     * @param messageColor GREEN for success, RED for errors, NAVY for information
     */
    private void showMessage(String title, String message, Color messageColor) {
        final Dialog dialog = new Dialog(this, title, true);
        dialog.setSize(440, 200);
        dialog.setResizable(false);
        dialog.setBackground(WHITE);
        dialog.setLayout(new BorderLayout());
        centerOnFrame(dialog);

        dialog.add(createMessagePanel(message, messageColor), BorderLayout.CENTER);

        Button okButton = createDialogButton("OK", NAVY);
        okButton.addActionListener(ev -> dialog.dispose());

        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        buttonPanel.setBackground(WHITE);
        buttonPanel.add(okButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true);   // waits here until the dialog is closed
    }

    /**
     * Shows a Yes / No question and returns true only if the user clicks Yes.
     * Closing the dialog with the X counts as No.
     */
    private boolean confirm(String title, String message) {
        // A one-item array lets the button code (below) change the answer
        final boolean[] answer = { false };

        final Dialog dialog = new Dialog(this, title, true);
        dialog.setSize(440, 230);
        dialog.setResizable(false);
        dialog.setBackground(WHITE);
        dialog.setLayout(new BorderLayout());
        centerOnFrame(dialog);

        dialog.add(createMessagePanel(message, NAVY), BorderLayout.CENTER);

        Button yesButton = createDialogButton("Yes, Vote", GREEN);
        Button noButton  = createDialogButton("Cancel", NAVY);

        yesButton.addActionListener(ev -> {
            answer[0] = true;
            dialog.dispose();
        });
        noButton.addActionListener(ev -> dialog.dispose());

        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        buttonPanel.setBackground(WHITE);
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true);   // waits here until the dialog is closed
        return answer[0];
    }

    // =====================================================================
    // PROGRAM ENTRY POINT (lets you test this screen on its own)
    // =====================================================================

    public static void main(String[] args) {
        // Opened on its own, nobody is logged in, so it shows "No voter is logged in".
        new VoterDashboard();
    }
}

/*
 * ---------------------------------------------------------------
 * HOW TO COMPILE AND RUN (VS Code terminal, inside the src folder)
 * ---------------------------------------------------------------
 *   cd src
 *   Remove-Item *.class     <- (PowerShell) clears old compiled files
 *   javac *.java            <- compiles all screens together
 *   java MainMenu           <- start here
 *
 * To test voting end to end (data lives in memory, so use ONE run):
 *   1. Admin Login (admin / 1234) -> Add at least 2 candidates -> Start Election -> Logout
 *   2. Voter Registration -> register a voter -> Back
 *   3. Voter Login -> log in -> select a candidate -> Cast Vote
 *   4. Logout, log in as admin, End Election -> View Results
 * ---------------------------------------------------------------
 */