package me.botsko.prism.commands;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionsQuery;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.actionlibs.QueryResult;
import me.botsko.prism.appliers.PrismProcessType;
import me.botsko.prism.appliers.Restore;
import me.botsko.prism.commandlibs.CallInfo;
import me.botsko.prism.commandlibs.PreprocessArgs;
import me.botsko.prism.commandlibs.SubHandler;

public class RestoreCommand implements SubHandler {
	
	/**
	 * 
	 */
	private Prism plugin;
	
	
	/**
	 * 
	 * @param plugin
	 * @return 
	 */
	public RestoreCommand(Prism plugin) {
		this.plugin = plugin;
	}
	
	
	/**
	 * Handle the command
	 */
	public void handle(CallInfo call) {
		
		QueryParameters parameters = PreprocessArgs.process( plugin, call.getSender(), call.getArgs(), PrismProcessType.RESTORE, 1 );
		if(parameters == null){
			return;
		}
		parameters.setStringFromRawArgs( call.getArgs(), 1 );
		
		// determine if defaults were used
		ArrayList<String> defaultsUsed = parameters.getDefaultsUsed();
		String defaultsReminder = "";
		if(!defaultsUsed.isEmpty()){
			defaultsReminder += " using defaults:";
			for(String d : defaultsUsed){
				defaultsReminder += " " + d;
			}
		}
		
		call.getSender().sendMessage( plugin.messenger.playerSubduedHeaderMsg("Preparing results..." + defaultsReminder) );
	
		ActionsQuery aq = new ActionsQuery(plugin);
		QueryResult results = aq.lookup( parameters, call.getSender() );
		if(!results.getActionResults().isEmpty()){
			
			call.getSender().sendMessage( plugin.messenger.playerHeaderMsg("Restoring changes...") );

			// Inform nearby players
			if( call.getSender() instanceof Player ){
				Player player = (Player) call.getSender();
				plugin.notifyNearby(player, parameters.getRadius(), player.getDisplayName() + " is re-applying block changes nearby. Just so you know.");
			}
			
			// Perform restore
			Restore rs = new Restore( plugin, call.getSender(), PrismProcessType.RESTORE, results.getActionResults(), parameters );
			rs.apply();
			
		} else {
			call.getSender().sendMessage( plugin.messenger.playerError( "Nothing found to restore. Try using /prism l (args) first." ) );
		}
	}
}