package net.frozenorb.potpvp.game.tournament;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.MatchState;
import net.frozenorb.potpvp.game.match.MatchTeam;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import net.frozenorb.potpvp.player.party.Party;
import net.frozenorb.potpvp.util.bukkit.files.ConfigFile;
import net.frozenorb.potpvp.util.bukkit.event.HalfHourEvent;
import net.frozenorb.potpvp.util.potpvp.PotPvPLang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class TournamentHandler implements Listener {

    public Tournament tournament = null;
    public static TournamentHandler instance;
    public static List<TournamentStatus> allStatuses;

    public TournamentHandler() {
        instance = this;
        PotPvPSI.getInstance().getCommandHandler().registerClass(this.getClass());
        Bukkit.getPluginManager().registerEvents(this, PotPvPSI.getInstance());
        Bukkit.getScheduler().scheduleSyncRepeatingTask(PotPvPSI.getInstance(), () -> {
            if (tournament != null) tournament.check();
        }, 20L, 20L);

        populateTournamentStatuses();
    }

    public boolean isInTournament(Party party) {
        return this.tournament != null && this.tournament.isInTournament(party);
    }

    public boolean isInTournament(Match match) {
        return this.tournament != null && this.tournament.getMatches().contains(match);
    }

    @Command(names={"tournament start"}, permission="tournament.create")
    public static void tournamentCreate(CommandSender sender, @Param(name="kit-type") KitType type, @Param(name="teamSize") int teamSize, @Param(name="requiredTeams") int requiredTeams) {
        if (instance.getTournament() != null) {
            sender.sendMessage(ChatColor.RED + "There's already an ongoing tournament!");
            return;
        }
        if (type == null) {
            sender.sendMessage(ChatColor.RED + "Kit type not found!");
            return;
        }
        if (teamSize < 1 || 10 < teamSize) {
            sender.sendMessage(ChatColor.RED + "Invalid team size range. Acceptable inputs: 1 -> 10");
            return;
        }
        if (requiredTeams < 4) {
            sender.sendMessage(ChatColor.RED + "Required teams must be at least 4.");
            return;
        }
        ConfigFile config = PotPvPSI.getInstance().getMessagesConfig();
        config.getStringList("TOURNAMENT.WAITING-PLAYERS")
                .stream()
                .map(s -> s.replace("%teamsize-requiredteams%", String.valueOf(teamSize < 3 ? teamSize * requiredTeams : requiredTeams)))
                .map(s -> s.replace("%kittype%", type.getColoredDisplayName()))
                .forEach(Bukkit::broadcastMessage);
        final Tournament tournament = new Tournament(type, teamSize, requiredTeams);
        instance.setTournament(tournament);
        new BukkitRunnable(){

            public void run() {
                if (instance.getTournament() == tournament) {
                    tournament.broadcastJoinMessage();
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(PotPvPSI.getInstance(), 1200L, 1200L);
    }

    @Command(names={"tournament join", "join", "jointournament"}, permission="")
    public static void tournamentJoin(Player sender) {
        if (instance.getTournament() == null) {
            sender.sendMessage(ChatColor.RED + "There is no running tournament to join.");
            return;
        }
        int tournamentTeamSize = instance.getTournament().getRequiredPartySize();
        if (!(instance.getTournament().getCurrentRound() == -1 && instance.getTournament().getBeginNextRoundIn() == 31 || instance.getTournament().getCurrentRound() == 0 && sender.hasPermission("tournaments.joinduringcountdown"))) {
            sender.sendMessage(ChatColor.RED + "This tournament is already in progress.");
            return;
        }
        Party senderParty = PotPvPSI.getInstance().getPartyHandler().getParty(sender);
        if (senderParty == null) {
            if (tournamentTeamSize == 1) {
                senderParty = PotPvPSI.getInstance().getPartyHandler().getOrCreateParty(sender);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have a team to join the tournament with!");
                return;
            }
        }
        int notInLobby = 0;
        int queued = 0;
        for (UUID member : senderParty.getMembers()) {
            if (!PotPvPSI.getInstance().getLobbyHandler().isInLobby(Bukkit.getPlayer(member))) {
                ++notInLobby;
            }
            if (PotPvPSI.getInstance().getQueueHandler().getQueueEntry(member) == null) continue;
            ++queued;
        }
        if (notInLobby != 0) {
            sender.sendMessage(ChatColor.RED.toString() + notInLobby + "member" + (notInLobby == 1 ? "" : "s") + " of your team aren't in the lobby.");
            return;
        }
        if (queued != 0) {
            sender.sendMessage(ChatColor.RED.toString() + notInLobby + "member" + (notInLobby == 1 ? "" : "s") + " of your team are currently queued.");
            return;
        }
        if (!senderParty.getLeader().equals(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You must be the leader of your team to join the tournament.");
            return;
        }
        if (instance.isInTournament(senderParty)) {
            sender.sendMessage(ChatColor.RED + "Your team is already in the tournament!");
            return;
        }
        if (senderParty.getMembers().size() != instance.getTournament().getRequiredPartySize()) {
            sender.sendMessage(ChatColor.RED + "You need exactly " + instance.getTournament().getRequiredPartySize() + " members in your party to join the tournament.");
            return;
        }
        if (PotPvPSI.getInstance().getQueueHandler().getQueueEntry(senderParty) != null) {
            sender.sendMessage(ChatColor.RED + "You can't join the tournament if your party is currently queued.");
            return;
        }
        senderParty.message(ChatColor.GREEN + "Joined the tournament.");
        instance.getTournament().addParty(senderParty);
    }

    @Command(names = { "tournament status", "tstatus", "status" }, permission = "")
    public static void tournamentStatus(CommandSender sender) {
        if (instance.getTournament() == null) {
            sender.sendMessage(ChatColor.RED + "There is no ongoing tournament to get the status of.");
            return;
        }

        sender.sendMessage(PotPvPLang.LONG_LINE);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Live &bTournament &7Fights"));
        sender.sendMessage("");
        List<Match> ongoingMatches = instance.getTournament().getMatches().stream().filter(m -> m.getState() != MatchState.TERMINATED).collect(Collectors.toList());

        for (Match match : ongoingMatches) {
            MatchTeam firstTeam = match.getTeams().get(0);
            MatchTeam secondTeam = match.getTeams().get(1);

            if (firstTeam.getAllMembers().size() == 1) {
                sender.sendMessage("  " + ChatColor.GRAY + "» " + ChatColor.AQUA + PotPvPSI.getInstance().uuidCache.name(firstTeam.getFirstMember()) + ChatColor.GRAY + " vs " + ChatColor.AQUA + PotPvPSI.getInstance().uuidCache.name(secondTeam.getFirstMember()));
            } else {
                sender.sendMessage("  " + ChatColor.GRAY + "» " + ChatColor.AQUA + PotPvPSI.getInstance().uuidCache.name(firstTeam.getFirstMember()) + ChatColor.GRAY + "'s team vs " + ChatColor.AQUA + PotPvPSI.getInstance().uuidCache.name(secondTeam.getFirstMember()) + ChatColor.GRAY + "'s team");
            }
        }
        sender.sendMessage(PotPvPLang.LONG_LINE);
    }

    @Command(names={"tournament cancel", "tcancel"}, permission="op")
    public static void tournamentCancel(CommandSender sender) {
        if (instance.getTournament() == null) {
            sender.sendMessage(ChatColor.RED + "There is no running tournament to cancel.");
            return;
        }
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c&lThe tournament was cancelled."));
        Bukkit.broadcastMessage("");
        instance.setTournament(null);
    }

    @Command(names={"tournament forcestart"}, permission="op")
    public static void tournamentForceStart(CommandSender sender) {
        if (instance.getTournament() == null) {
            sender.sendMessage(ChatColor.RED + "There is no tournament to force start.");
            return;
        }
        if (instance.getTournament().getCurrentRound() != -1 || instance.getTournament().getBeginNextRoundIn() != 31) {
            sender.sendMessage(ChatColor.RED + "This tournament is already in progress.");
            return;
        }
        instance.getTournament().start();
        sender.sendMessage(ChatColor.GREEN + "Force started tournament.");
    }

    public void populateTournamentStatuses() {
        List<KitType> viewableKits = KitType.getAllTypes().stream().filter(kit -> !kit.isHidden()).collect(Collectors.toList());
        allStatuses.add(new TournamentStatus(0, ImmutableList.of(1), ImmutableList.of(16, 32), viewableKits));
        allStatuses.add(new TournamentStatus(250, ImmutableList.of(1), ImmutableList.of(32), viewableKits));
        allStatuses.add(new TournamentStatus(300, ImmutableList.of(1), ImmutableList.of(48, 64), ImmutableList.of(KitType.teamFight)));
        allStatuses.add(new TournamentStatus(400, ImmutableList.of(1), ImmutableList.of(64), ImmutableList.of(KitType.teamFight)));
        allStatuses.add(new TournamentStatus(500, ImmutableList.of(1), ImmutableList.of(128), ImmutableList.of(KitType.teamFight)));
        allStatuses.add(new TournamentStatus(600, ImmutableList.of(1), ImmutableList.of(128), ImmutableList.of(KitType.teamFight)));
        allStatuses.add(new TournamentStatus(700, ImmutableList.of(1), ImmutableList.of(128), ImmutableList.of(KitType.teamFight)));
        allStatuses.add(new TournamentStatus(800, ImmutableList.of(1), ImmutableList.of(128), ImmutableList.of(KitType.teamFight)));
    }

    @EventHandler
    public void onHalfHour(HalfHourEvent event) {
        if (instance.getTournament() != null) {
            return;
        }
        TournamentStatus status = TournamentStatus.forPlayerCount(Bukkit.getOnlinePlayers().size());
        int teamSize = status.getTeamSizes().get(ThreadLocalRandom.current().nextInt(status.getTeamSizes().size()));
        int teamCount = status.getTeamCounts().get(ThreadLocalRandom.current().nextInt(status.getTeamCounts().size()));
        KitType kitType = status.getKitTypes().get(ThreadLocalRandom.current().nextInt(status.getKitTypes().size()));
        final Tournament tournament = new Tournament(kitType, teamSize, teamCount);
        instance.setTournament(tournament);
        Bukkit.getScheduler().runTaskLater(PotPvPSI.getInstance(), () -> {
            if (tournament == instance.getTournament() && instance.getTournament() != null && instance.getTournament().getCurrentRound() == -1) {
                instance.getTournament().start();
            }
        }, 3600L);
        new BukkitRunnable(){

            public void run() {
                if (instance.getTournament() == tournament) {
                    tournament.broadcastJoinMessage();
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(PotPvPSI.getInstance(), 1200L, 1200L);
    }

    public Tournament getTournament() {
        return this.tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    static {
        allStatuses = Lists.newArrayList();
    }

    public static class TournamentStatus {
        public int minimumPlayerCount;
        public List<Integer> teamSizes;
        public List<Integer> teamCounts;
        public List<KitType> kitTypes;

        public TournamentStatus(int minimumPlayerCount, List<Integer> teamSizes, List<Integer> teamCounts, List<KitType> kitTypes) {
            this.minimumPlayerCount = minimumPlayerCount;
            this.teamSizes = teamSizes;
            this.teamCounts = teamCounts;
            this.kitTypes = kitTypes;
        }

        public static TournamentStatus forPlayerCount(int playerCount) {
            for (int i = allStatuses.size() - 1; 0 <= i; --i) {
                if (allStatuses.get(i).minimumPlayerCount > playerCount) continue;
                return allStatuses.get(i);
            }
            throw new IllegalArgumentException("No suitable sizes found!");
        }

        public int getMinimumPlayerCount() {
            return this.minimumPlayerCount;
        }

        public List<Integer> getTeamSizes() {
            return this.teamSizes;
        }

        public List<Integer> getTeamCounts() {
            return this.teamCounts;
        }

        public List<KitType> getKitTypes() {
            return this.kitTypes;
        }
    }
}