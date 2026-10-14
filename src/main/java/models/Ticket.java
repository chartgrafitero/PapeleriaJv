/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.time.LocalDateTime;

/**
 *
 * @author chart
 */
public class Ticket {
    

    private long idTicket;
    private LocalDateTime fecha;
    private String personalQuienAtendio;

    public Ticket() {
    }

    public Ticket(String personalQuienAtendio) {
        this.fecha = LocalDateTime.now();
        this.personalQuienAtendio = personalQuienAtendio;
    }

    public Ticket(long idTicket, LocalDateTime fecha,
                  String personalQuienAtendio) {
        this.idTicket = idTicket;
        this.fecha = fecha;
        this.personalQuienAtendio = personalQuienAtendio;
    }

    public long getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(long idTicket) {
        this.idTicket = idTicket;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getPersonalQuienAtendio() {
        return personalQuienAtendio;
    }

    public void setPersonalQuienAtendio(String personalQuienAtendio) {
        this.personalQuienAtendio = personalQuienAtendio;
    }
}
