package com.git.opengds.validator.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;

import com.git.gdsbuilder.FileRead.dxf.writer.QA10FileWriter;
import com.git.gdsbuilder.FileRead.ngi.writer.QA20FileWriter;
import com.git.gdsbuilder.type.qa10.collection.QA10LayerCollection;
import com.git.gdsbuilder.type.qa20.collection.QA20LayerCollection;
import com.git.opengds.parser.error.ErrorLayerDXFExportParser;
import com.git.opengds.parser.error.ErrorLayerNGIExportParser;
import com.git.opengds.validator.dbManager.ErrorLayerDBQueryManager;
import com.git.opengds.validator.persistence.ErrorLayerDAO;

@Service
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/**/*.xml" })
public class ErrorLayerExportServiceImpl implements ErrorLayerExportService {

	@Inject
	private ErrorLayerDAO errLayerDAO;

	@Autowired
	private DataSourceTransactionManager txManager;

	@Override
	public void exportErrorLayer(String exportOption) throws IOException {

		String tableName = "\"" + "err_" + "ngi_33611044" + "\"";
		String exportType = "dxf";

		ErrorLayerDBQueryManager dbManager = new ErrorLayerDBQueryManager();
		HashMap<String, Object> selectQuery = dbManager.selectAllErrorFeaturesQuery(tableName);
		List<HashMap<String, Object>> errAllFeatures = errLayerDAO.selectAllErrorFeatures(selectQuery);
		if (exportType.equals("ngi")) {
			QA20LayerCollection qa20LayerCollection = ErrorLayerNGIExportParser.parseQA20LayerCollection(tableName,
					errAllFeatures);
			QA20FileWriter qa20Writer = new QA20FileWriter();
			qa20Writer.writeNGIFile(qa20LayerCollection);
		} else if (exportType.equals("dxf")) {
			QA10LayerCollection qa10LayerCollection = ErrorLayerDXFExportParser.parseQA10LayerCollection(tableName,
					errAllFeatures);

			QA10FileWriter r = new QA10FileWriter();
			r.writeDxfFile(qa10LayerCollection);
		}
	}
}
