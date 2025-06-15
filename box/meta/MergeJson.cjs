"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g = Object.create((typeof Iterator === "function" ? Iterator : Object).prototype);
    return g.next = verb(0), g["throw"] = verb(1), g["return"] = verb(2), typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (g && (g = 0, op[0] && (_ = 0)), _) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.MergeJSON = void 0;
var path = require("path");
var fs = require("fs");
var fsPromise = fs.promises;
var MergeJSON = /** @class */ (function () {
    function MergeJSON() {
    }
    MergeJSON.removeDir = function (path) {
        return __awaiter(this, void 0, void 0, function () {
            var files;
            return __generator(this, function (_a) {
                if (fs.existsSync(path)) {
                    files = fs.readdirSync(path);
                    if (files.length > 0) {
                        files.forEach(function (filename) {
                            if (fs.statSync(path + "/" + filename).isDirectory()) {
                                MergeJSON.removeDir(path + "/" + filename);
                            }
                            else {
                                fs.unlinkSync(path + "/" + filename);
                            }
                        });
                        fs.rmdirSync(path);
                    }
                    else {
                        fs.rmdirSync(path);
                    }
                }
                else {
                    console.log("Directory path not found.");
                }
                return [2 /*return*/];
            });
        });
    };
    MergeJSON.prototype.combineMeta = function () {
        return __awaiter(this, void 0, void 0, function () {
            var rootFolderPaths, uri;
            var _this = this;
            return __generator(this, function (_a) {
                rootFolderPaths = ["box"];
                uri = path.resolve(__dirname);
                console.log("combineMeta uri", uri);
                //delete existing generated files
                MergeJSON.removeDir("".concat(uri, "/generated"));
                // console.log("existing generated files deleted")
                // console.log("combineMeta uri tes", uri)
                rootFolderPaths.forEach(function (folderRoot) {
                    fs.readdir("".concat(uri, "/schema/").concat(folderRoot), function (err, files) {
                        files.forEach(function (file) { return __awaiter(_this, void 0, void 0, function () {
                            return __generator(this, function (_a) {
                                switch (_a.label) {
                                    case 0:
                                        if (!file.includes(".json")) return [3 /*break*/, 2];
                                        // console.log("file", file)
                                        return [4 /*yield*/, this.checkAndMergeJSON(file, folderRoot)];
                                    case 1:
                                        // console.log("file", file)
                                        _a.sent();
                                        _a.label = 2;
                                    case 2: return [2 /*return*/];
                                }
                            });
                        }); });
                    });
                });
                return [2 /*return*/];
            });
        });
    };
    MergeJSON.prototype.checkAndMergeJSON = function (file, folderRoot) {
        return __awaiter(this, void 0, void 0, function () {
            var uri, parentDir, mainFilePath, id, sourceFilePath, forderPath, data, dirname, exist;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        uri = path.resolve(__dirname);
                        parentDir = path.dirname(uri);
                        mainFilePath = "".concat(parentDir, "/node_modules/box/dist/src/box/meta/generated/").concat(folderRoot, "/").concat(file) //file path to be generated
                        ;
                        id = file.replace(".json", '');
                        sourceFilePath = "".concat(uri, "/schema/").concat(folderRoot, "/").concat(id, ".json") // main source file path
                        ;
                        forderPath = "".concat(uri, "/schema/").concat(folderRoot, "/").concat(id);
                        data = require(sourceFilePath);
                        data = this.replaceObjectJSON(data, forderPath);
                        dirname = path.dirname(mainFilePath);
                        return [4 /*yield*/, fs.existsSync(dirname)];
                    case 1:
                        exist = _a.sent();
                        if (!!exist) return [3 /*break*/, 3];
                        return [4 /*yield*/, fsPromise.mkdir(dirname, { recursive: true })];
                    case 2:
                        _a.sent();
                        _a.label = 3;
                    case 3: return [4 /*yield*/, fsPromise.writeFile(mainFilePath, JSON.stringify(data))]; //create and write json
                    case 4:
                        _a.sent(); //create and write json
                        return [2 /*return*/];
                }
            });
        });
    };
    /*
    check for key and object value and replace json bject
    @param data data map
    @param folderPath folderpath
    */
    MergeJSON.prototype.replaceObjectJSON = function (data, forderPath) {
        var newObj = {};
        var templateKeys = Object.keys(data);
        for (var i = 0; i < templateKeys.length; i++) {
            var currentKeyValue = data[templateKeys[i]];
            if (currentKeyValue != null && typeof currentKeyValue == 'object') { // If there is inner object, then recursively form the object
                if (Array.isArray(currentKeyValue)) {
                    var currentKeyValueArr = [];
                    for (var j = 0; j < currentKeyValue.length; j++) {
                        var keyValueObj = currentKeyValue[j];
                        if (currentKeyValue[j] != null && typeof currentKeyValue[j] == 'object') {
                            currentKeyValueArr.push(this.replaceObjectJSON(keyValueObj, forderPath));
                        }
                        else {
                            currentKeyValueArr.push(keyValueObj);
                        }
                    }
                    newObj[templateKeys[i]] = currentKeyValueArr;
                }
                else {
                    var innerObj = this.replaceObjectJSON(currentKeyValue, forderPath);
                    if (innerObj && Object.keys(innerObj).length > 0) {
                        newObj[templateKeys[i]] = innerObj;
                    }
                }
            }
            else if (typeof currentKeyValue == 'number' || typeof currentKeyValue == 'boolean') {
                newObj[templateKeys[i]] = currentKeyValue;
            }
            else {
                var value = this.replaceJSONFileToTemplate(currentKeyValue, forderPath);
                if ((value && value != "") || value == null) {
                    newObj[templateKeys[i]] = value;
                }
            }
        }
        return newObj;
    };
    /*
    check for file and replace JSON file
    @param template value string
    @param folderPath folderpath
    */
    MergeJSON.prototype.replaceJSONFileToTemplate = function (template, folderPath) {
        if (typeof template == 'boolean' || template == null)
            return template;
        var templateVars = template.match(/\$\{include[^\}]+\.json}/g);
        if (templateVars == null)
            return template;
        var templateVar = templateVars[0];
        var file = templateVar.substring(10, templateVar.length - 1); // remove ${include|};
        try {
            // Use path.join and path.resolve for correct path construction
            var filePath = path.resolve(folderPath, file);
            if (fs.existsSync(filePath)) {
                var jsonObject = require(filePath);
                var data = this.replaceObjectJSON(jsonObject, folderPath);
                template = data;
            }
            else {
                console.warn("File not found: ".concat(filePath));
            }
        }
        catch (error) {
            console.warn("Error in replacing file %s Error: %s", file, error);
            template = template;
        }
        return template;
    };
    return MergeJSON;
}());
exports.MergeJSON = MergeJSON;
var merge = new MergeJSON();
merge.combineMeta();
