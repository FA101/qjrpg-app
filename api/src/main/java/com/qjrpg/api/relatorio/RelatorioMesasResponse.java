package com.qjrpg.api.relatorio;
import java.util.List;
import java.util.Map;

public record RelatorioMesasResponse(List<RelatorioMesaItem> mesas, long total,
                                      Map<String, Long> totalPorTipoJogo) {}
