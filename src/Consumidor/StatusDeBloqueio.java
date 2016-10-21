/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Consumidor;

/**
 *
 * @author Leonardo
 */
public enum StatusDeBloqueio {
    statusUnlocked(0),
    statusExclusiveLocked(1),
    statusSharedLock(2);

    private int status;

    StatusDeBloqueio() {
    }

    StatusDeBloqueio(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    
}
