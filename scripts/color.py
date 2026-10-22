#!/usr/bin/env python3
"""Lee el color de uno o varios puntos de un PNG. Sin dependencias.

    python3 scripts/color.py captura.png 540,130 540,30

Sirve para comparar una captura del emulador con el frame del Figma en los mismos
puntos: el ojo no distingue #F7F8F5 de #FFFFFF, y esto sí.
"""
import sys
import zlib
import struct


def leer(ruta):
    datos = open(ruta, 'rb').read()
    pos, idat, ancho, alto, tipo = 8, b'', None, None, None
    while pos < len(datos):
        largo = struct.unpack('>I', datos[pos:pos + 4])[0]
        clase = datos[pos + 4:pos + 8]
        cuerpo = datos[pos + 8:pos + 8 + largo]
        if clase == b'IHDR':
            ancho, alto, _, tipo = struct.unpack('>IIBB', cuerpo[:10])
        elif clase == b'IDAT':
            idat += cuerpo
        pos += 12 + largo
    canales = 4 if tipo == 6 else 3
    return ancho, alto, canales, zlib.decompress(idat)


def colores(ruta, puntos):
    ancho, alto, canales, crudo = leer(ruta)
    paso = ancho * canales
    ultimo = max(y for _, y in puntos)
    previa, salida, i = bytearray(paso), {}, 0
    for y in range(alto):
        filtro = crudo[i]
        i += 1
        linea = bytearray(crudo[i:i + paso])
        i += paso
        for x in range(paso):
            a = linea[x - canales] if x >= canales else 0
            b = previa[x]
            c = previa[x - canales] if x >= canales else 0
            if filtro == 1:
                linea[x] = (linea[x] + a) & 255
            elif filtro == 2:
                linea[x] = (linea[x] + b) & 255
            elif filtro == 3:
                linea[x] = (linea[x] + (a + b) // 2) & 255
            elif filtro == 4:
                p = a + b - c
                pa, pb, pc = abs(p - a), abs(p - b), abs(p - c)
                linea[x] = (linea[x] + (a if pa <= pb and pa <= pc else b if pb <= pc else c)) & 255
        for px, py in puntos:
            if py == y:
                o = px * canales
                salida[(px, py)] = '#%02X%02X%02X' % (linea[o], linea[o + 1], linea[o + 2])
        previa = linea
        if y > ultimo:
            break
    return ancho, alto, salida


if __name__ == '__main__':
    if len(sys.argv) < 3:
        print(__doc__)
        raise SystemExit(2)
    ruta = sys.argv[1]
    puntos = [tuple(int(n) for n in p.split(',')) for p in sys.argv[2:]]
    ancho, alto, salida = colores(ruta, puntos)
    print(f'{ruta}  ({ancho} x {alto})')
    for (x, y) in puntos:
        print(f'  {x},{y} -> {salida.get((x, y), "(fuera de la imagen)")}')
