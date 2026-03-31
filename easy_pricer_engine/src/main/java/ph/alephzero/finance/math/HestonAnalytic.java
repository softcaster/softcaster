/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.math;

import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.complex.Complex;

public class HestonAnalytic {

    public static double calculateCallPrice(HestonInput input) {
       // Calcolo P1 e P2 tramite integrazione
        double p1 = 0.5 + (1.0 / Math.PI) * integrateProbability(1,input);
        double p2 = 0.5 + (1.0 / Math.PI) * integrateProbability(2,input);

        // Formula finale della Call: S0*P1 - K*exp(-rT)*P2
        return input.S0 * p1 - input.K * Math.exp(-input.r * input.T) * p2;    }

    private static double integrateProbability(int type,HestonInput input) {
        UnivariateFunction integrand = phi -> {
            if (phi < 1e-6) return 0; // Evita divisione per zero
            Complex iPhi = new Complex(0, phi);
            Complex charFunc = calculateCharacteristicFunction(phi, type,input);
            // Re[ exp(-i*phi*ln(K)) * f(phi) / (i*phi) ]
            Complex numerator = charFunc.multiply(iPhi.multiply(Math.log(input.K)).negate().exp());
            return numerator.divide(iPhi).getReal();
        };

        SimpsonIntegrator integrator = new SimpsonIntegrator();
        // Integriamo da quasi 0 a 100 (sufficiente per la convergenza nella maggior parte dei casi)
        return integrator.integrate(100000, integrand, 0.0001, 100.0);
    }
    private static Complex calculateCharacteristicFunction(double phi, int type, HestonInput input) {
        // Parametri ausiliari basati sul tipo (P1 o P2)input.
        double u = (type == 1) ? 0.5 : -0.5;
        double b = (type == 1) ? input.kappa - input.rho * input.sigma : input.kappa;

        Complex iPhi = new Complex(0, phi);

        // d = sqrt((rho * sigma * phi * i - b)^2 - sigma^2 * (2 * u * phi * i - phi^2))
        Complex d = iPhi.multiply(input.rho * input.sigma).subtract(b).pow(2)
                .subtract(iPhi.multiply(2 * u).subtract(phi * phi).multiply(input.sigma * input.sigma))
                .sqrt();

        // g = (b - rho * sigma * phi * i + d) / (b - rho * sigma * phi * i - d)
        Complex b_minus_rho_sigma_phi_i = new Complex(b, 0).subtract(iPhi.multiply(input.rho * input.sigma));
        Complex g = b_minus_rho_sigma_phi_i.add(d).divide(b_minus_rho_sigma_phi_i.subtract(d));

        // Espressioni per C(T) e D(T)
        Complex expDT = d.multiply(input.T).exp();

        // D_T = ((b - rho * sigma * phi * i + d) / sigma^2) * ((1 - exp(d*T)) / (1 - g * exp(d*T)))
        Complex DT = b_minus_rho_sigma_phi_i.add(d).divide(input.sigma * input.sigma)
                .multiply(new Complex(1, 0).subtract(expDT))
                .divide(new Complex(1, 0).subtract(g.multiply(expDT)));

        // CT = (r * phi * i * T) + (a / sigma^2) * ((b - rho * sigma * phi * i + d) * T - 2 * log((1 - g * exp(d*T)) / (1 - g)))
        double a = input.kappa * input.theta;
        Complex logPart = new Complex(1, 0).subtract(g.multiply(expDT))
                .divide(new Complex(1, 0).subtract(g)).log();

        Complex CT = iPhi.multiply(input.r * input.T)
                .add(new Complex(a / (input.sigma * input.sigma), 0)
                        .multiply(b_minus_rho_sigma_phi_i.add(d).multiply(input.T).subtract(logPart.multiply(2))));

        // f(phi) = exp(C(T) + D(T) * v0 + i * phi * ln(S0))
        return CT.add(DT.multiply(input.v0)).add(iPhi.multiply(Math.log(input.S0))).exp();
    }

}
