package pl.rezerveo.booking.slot.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServiceType {

    OIL_CHANGE("Wymiana oleju silnikowego"),
    TIRE_REPLACEMENT("Wymiana opon / kół"),
    GENERAL_CHECKUP("Przegląd ogólny pojazdu"),
    BRAKE_PAD_REPLACEMENT("Wymiana klocków hamulcowych"),
    BATTERY_REPLACEMENT("Wymiana akumulatora"),
    AIR_FILTER_REPLACEMENT("Wymiana filtra powietrza"),
    FUEL_FILTER_REPLACEMENT("Wymiana filtra paliwa"),
    SPARK_PLUG_REPLACEMENT("Wymiana świec zapłonowych"),
    ENGINE_DIAGNOSTICS("Diagnostyka silnika"),
    WHEEL_ALIGNMENT("Ustawienie geometrii kół"),
    AIR_CONDITIONING_SERVICE("Serwis klimatyzacji"),
    TRANSMISSION_SERVICE("Serwis skrzyni biegów"),
    SUSPENSION_REPAIR("Naprawa zawieszenia"),
    COOLANT_FLUSH("Wymiana płynu chłodniczego"),
    EXHAUST_SYSTEM_REPAIR("Naprawa układu wydechowego"),
    ELECTRICAL_SYSTEM_DIAGNOSTICS("Diagnostyka układu elektrycznego"),
    BRAKE_FLUID_REPLACEMENT("Wymiana płynu hamulcowego"),
    CHAIN_REPLACEMENT("Wymiana łańcucha napędowego"),
    LIGHT_BULB_REPLACEMENT("Wymiana żarówki / oświetlenia"),
    DETAILING_SERVICE("Mycie i detailing pojazdu");

    private final String description;
}