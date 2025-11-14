package com.tlse1.twodgame.Attacks;

import com.tlse1.twodgame.entities.Entity;
import com.tlse1.twodgame.entities.Character;
import com.tlse1.twodgame.entities.Player;
import com.tlse1.twodgame.entities.Enemy;
import com.tlse1.twodgame.utils.Direction;

public class MeleeAttack extends Attack {
    

    public MeleeAttack(float damage, float range, float delay) {
        super(damage, range, delay);
    }
    
    @Override
    public void execute(Object attacker, Object target) {
        System.out.println(attacker + " attaque " + target + " en mêlée pour " + damage + " dégâts");
        // Logique d'attaque ici
    }
}