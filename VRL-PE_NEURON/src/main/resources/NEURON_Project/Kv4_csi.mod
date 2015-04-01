:Kv4 CSI Markov model from Fineberg, Ritter and Covarrubias J Gen Physiol (2012)
:for scheme see Fig. S1 and Table S1 or file "IonChannelLab Files/Kv4 CSI.ichl"
:based on Amarillo et al. J Physiol (2008) and Migliore et al J Comp Neurosci (1999)
:last edited 10-06-2012 by DMR
COMMENT 
edited so that values are consistent with Kuznetsovas model
ENDCOMMENT


NEURON {
	SUFFIX kv4csi
	USEION k READ ek,ik WRITE ik
        RANGE g, gmax				
}

UNITS {
	(mA) = (milliamp)
	(mV) = (millivolt)

} 

PARAMETER {
    gmax = 0.00     	(S/cm2)
	ek=-90                  (mV)
    celsius         	(deg C)			
	F = 9.6485e4 				:Faraday constant
    R = 8.3145e3 				:Gas constant
	a = 7			(/ms)		:alpha transitions
	za = 0.315646648				
    b = .090		(/ms)		:beta transitions
    zb = -2.062276				
	c = 1.01216107		(/ms)		:gamma transition
	zc = 0.500095665
	d = 2.498881		(/ms)		:delta transition
	zd = -1.1546687
	k = 7.69049072		(/ms)		:epsilon transition
	zk = 0.05502051
	l = 4.38562354		(/ms)		:phi transition
	zl = -0.07092366 
	f = 0.277130485				:closed-state inactivation allosteric factor f
	q = 1.01314807				:closed-state inactivation allosteric factor g
	kci = 0.121900093	(/ms)		:closed to inactivated transitions
	kic = 0.0017935468 	(/ms)		:inactivated to closed transitions
}

ASSIGNED {
     	v    (mV)
     	g    (S/cm2)
      	ik   (mA/cm2)
      	kC01f  (/ms)
      	kC01b  (/ms)
		kC12f  (/ms)
     	kC12b  (/ms)
		kC23f  (/ms)
     	kC23b  (/ms)
		kC34f  (/ms)
     	kC34b  (/ms)
		kC45f  (/ms)
 		kC45b  (/ms)
		kCOf  (/ms)
      	kCOb  (/ms)
      	kCI0f  (/ms)
      	kCI0b  (/ms)
		kCI1f  (/ms)
     	kCI1b  (/ms)
		kCI2f  (/ms)
     	kCI2b  (/ms)
		kCI3f  (/ms)
     	kCI3b  (/ms)
		kCI4f  (/ms)
 		kCI4b  (/ms)
		kCI5f  (/ms)
      	kCI5b  (/ms)
      	kI01f  (/ms)
      	kI01b  (/ms)
		kI12f  (/ms)
     	kI12b  (/ms)
		kI23f  (/ms)
     	kI23b  (/ms)
		kI34f  (/ms)
     	kI34b  (/ms)
		kI45f  (/ms)
 		kI45b  (/ms)
}

STATE { C0 C1 C2 C3 C4 C5 I0 I1 I2 I3 I4 I5 O }
BREAKPOINT {
      SOLVE states METHOD sparse
      g = gmax * O
      ik = g * (v - ek)
}

INITIAL { SOLVE states STEADYSTATE sparse}

KINETIC states {   		
        rates(v)
	~C0 <-> C1 (kC01f,kC01b)
	~C1 <-> C2 (kC12f,kC12b)
	~C2 <-> C3 (kC23f,kC23b)
	~C3 <-> C4 (kC34f,kC34b)
	~C4 <-> C5 (kC45f,kC45b)
	~C5 <-> O (kCOf,kCOb)
	~C0 <-> I0 (kCI0f,kCI0b)
	~C1 <-> I1 (kCI1f,kCI1b)
	~C2 <-> I2 (kCI2f,kCI2b)
	~C3 <-> I3 (kCI3f,kCI3b)
	~C4 <-> I4 (kCI4f,kCI4b)
	~C5 <-> I5 (kCI5f,kCI5b)
	~I0 <-> I1 (kI01f,kI01b)
	~I1 <-> I2 (kI12f,kI12b)
	~I2 <-> I3 (kI23f,kI23b)
	~I3 <-> I4 (kI34f,kI34b)
	~I4 <-> I5 (kI45f,kI45b)
	CONSERVE C0+C1+C2+C3+C4+C5+I0+I1+I2+I3+I4+I5+O=1
}

PROCEDURE rates(v(millivolt)) {

      kC01f = 4*a*exp(za*v*F/(R*(273.16+celsius)))		:closed to open pathway transitions
      kC01b = b*exp(zb*v*F/(R*(273.16+celsius)))		:273.16 K = 0.01degCelsius
      kC12f = 3*a*exp(za*v*F/(R*(273.16+celsius)))
      kC12b = 2*b*exp(zb*v*F/(R*(273.16+celsius)))
      kC23f = 2*a*exp(za*v*F/(R*(273.16+celsius)))
      kC23b = 3*b*exp(zb*v*F/(R*(273.16+celsius)))
      kC34f = a*exp(za*v*F/(R*(273.16+celsius)))
      kC34b = 4*b*exp(zb*v*F/(R*(273.16+celsius)))
      kC45f = c*exp(zc*v*F/(R*(273.16+celsius)))
      kC45b = d*exp(zd*v*F/(R*(273.16+celsius)))
      kCOf = k*exp(zk*v*F/(R*(273.16+celsius)))
      kCOb = l*exp(zl*v*F/(R*(273.16+celsius)))
      kCI0f = kci*(f^4)					:closed to inactivated transitions
      kCI0b = kic/(f^4) 
      kCI1f = kci*(f^3)
      kCI1b = kic/(f^3)
      kCI2f = kci*(f^2)
      kCI2b = kic/(f^2)
      kCI3f = kci*(f)
      kCI3b = kic/(f)
      kCI4f = kci
      kCI4b = kic
      kCI5f = kci*q
      kCI5b = kic/q
      kI01f = 4*(1/f)*a*exp(za*v*F/(R*(273.16+celsius)))	:closed to inactivated transitions
      kI01b = d*b*exp(zb*v*F/(R*(273.16+celsius)))
      kI12f = 3*(1/f)*a*exp(za*v*F/(R*(273.16+celsius)))
      kI12b = 2*f*b*exp(zb*v*F/(R*(273.16+celsius)))
      kI23f = 2*(1/f)*a*exp(za*v*F/(R*(273.16+celsius)))
      kI23b = 3*f*b*exp(zb*v*F/(R*(273.16+celsius)))
      kI34f = (1/f)*a*exp(za*v*F/(R*(273.16+celsius)))
      kI34b = 4*f*b*exp(zb*v*F/(R*(273.16+celsius)))
      kI45f = q*c*exp(zc*v*F/(R*(273.16+celsius)))
      kI45b = (1/q)*d*exp(zd*v*F/(R*(273.16+celsius)))
}

