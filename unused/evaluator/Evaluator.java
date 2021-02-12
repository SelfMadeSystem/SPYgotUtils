package uwu.smsgamer.spygotutils.evaluator;

import org.bukkit.OfflinePlayer;

import java.util.HashMap;

/**
 * JS/Py like evaluator for simple arithmetic based operations.
 */
public class Evaluator {
    public HashMap<String, EvalVar<?>> varMap = new HashMap<>();
    public OfflinePlayer player;

    public Evaluator(OfflinePlayer player, EvalVar<?>... defVars) {
        this.player = player;
        for (EvalVar<?> defVar : defVars) addVar(defVar);
    }

    public <T> void addVar(EvalVar<T> evalVar) {
        varMap.put(evalVar.getName(), evalVar);
    }

    public EvalVar<?> eval(String str) {
        return new EvalTokenizer(str).tokenize().parseToVars().parseToFuns().sortFuns().tokens.get(0).toVar(this);
    }
}
