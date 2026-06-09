/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.easy_pricer_eod.services;

public interface MicroserviceDescriptor {

    String getServiceName();       // Es. "MTM-Service", "Rest-Server"

    String getJarPath();           // Percorso del file .jar

    String getActiveProfile();     // Profilo Spring (es. "dev", "prod")

    String[] getAdditionalArgs();  // Argomenti extra (es. "--server.port=8081")
}
